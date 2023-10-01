package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.ifTipOfTheDayDialogPresent
import com.intellij.openapi.ui.ComboBox
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.JButtonFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.steps.CommonSteps
import com.intellij.remoterobot.utils.DefaultHttpClient
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.WaitForConditionTimeoutException
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitForIgnoringError
import com.intellij.util.TimeoutUtil.sleep
import org.apache.commons.io.file.PathUtils
import org.apache.commons.lang.StringEscapeUtils
import org.junit.Assume
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.ConnectException
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import javax.imageio.ImageIO

open class BaseTestProjectTests {
    @field:TempDir
    lateinit var tempDir: Path

    lateinit var projectPath: Path

    protected val droidAutomatorSnapshotFile by lazy { """$projectPath/demo/snapshots/dump.uix""" }

    protected val remoteRobot: RemoteRobot by lazy {
        try {
            val httpClient = DefaultHttpClient.client.newBuilder()
                .readTimeout(Duration.ofMinutes(1)) // prevent timing out on Macs when entering paths in Open dialogs
                .build()
            RemoteRobot(
                "http://127.0.0.1:8082",
                httpClient
            ).apply {
                runJs("")
            }
        } catch (e: ConnectException) {
            Assume.assumeNoException("IDE should be running in UI tests mode", e)
            throw e
        }
    }
    protected val commonSteps by lazy { CommonSteps(remoteRobot) }

    @BeforeEach
    fun openTestProject() {
        PathUtils.cleanDirectory(tempDir)

        projectPath = tempDir.resolve("project")
        extractResourceRecursively("", "project", projectPath)

        with(remoteRobot) {
            cancelModalIfNeeded()

            find(
                CommonContainerFixture::class.java,
                Locators.byType("WelcomeScreen"),
                Duration.ofSeconds(10)
            ).apply {
                button(
                    byXpath(
                        "//div[contains(@class, 'Button')" +
                                " and (contains(@accessiblename,'Open') or @text='Open')" +
                                " and not(div[contains(@class, 'Button')])]"
                    ),
                    Duration.ofSeconds(10)
                ).clickWhenEnabled()
            }
            find<CommonContainerFixture>(
                Locators.byProperties(Locators.XpathProperty.SIMPLE_CLASS_NAME to "DialogRootPane"),
                Duration.ofSeconds(10)
            ).apply {
                val fullProjectPath = projectPath.toAbsolutePath()

                comboBox(Locators.byType(ComboBox::class.java)).apply {
                    // workaround flakiness on Mac
                    (0..4).takeUnless {
                            click()
                            try {
                                waitForIgnoringError {
                                    hasFocus
                                }
                                true
                            } catch (e: WaitForConditionTimeoutException) {
                                false
                            }
                        }
                    keyboard {
                        val escapedPath = StringEscapeUtils.escapeJavaScript(fullProjectPath.toString())
                            .replace("\\\"", "\"") // workaround escaping in enterText
                        // workaround opening external apps or emoji keyboard on Mac
                        sleep(2_000)
                        backspace()
                        sleep(2_000)
                        // delayBetweenCharsInMs here is also part of the workaround
                        enterText(escapedPath, delayBetweenCharsInMs = 100)
                    }
                }
                sleep(10_000)
                // workaround flakiness on Mac
                (0..4).takeUnless {
                    button("OK").click()
                    try {
                        waitForIgnoringError(Duration.ofSeconds(10)) {
                            remoteRobot.callJs(
                                runInEdt = true, script =
                                """
                                importPackage(com.intellij.openapi.project.ex)
                                ProjectManagerEx.getInstance().openProjects.length > 0
                            """.trimIndent()
                            )
                        }
                        true
                    } catch (e: WaitForConditionTimeoutException) {
                        false
                    }
                }
            }

            sleep(2_000)

            idea {
                ifTipOfTheDayDialogPresent() {
                    close()
                }
                commonSteps.waitForSmartMode(1)
            }
        }
    }

    private fun RemoteRobot.cancelModalIfNeeded() {
        try {
            find<JButtonFixture>(JButtonFixture.byText("Cancel")).clickWhenEnabled()
        } catch (e: WaitForConditionTimeoutException) {
            // no-op
        }
    }

    @AfterEach
    fun closeTestProject(testInfo: TestInfo): Unit = with(remoteRobot) {
        takeAndWriteScreenshot(testInfo)

        commonSteps.invokeAction("CloseProject")
        sleep(3_000) // wait until invokeAction is executed
    }

    private fun RemoteRobot.takeAndWriteScreenshot(testInfo: TestInfo) {
        val screenshotDir = File("screenshot").apply {
            mkdirs()
        }
        val baseName = testInfo.testMethod
            .map { it.name }
            .orElse(testInfo.displayName)
            .replace(Regex("\\W"), "_")
        ImageIO.write(getScreenshot(), "png", File(screenshotDir, "$baseName.png"))
    }

    private fun extractResourceRecursively(
        baseDir: String,
        name: String,
        basePath: Path
    ) {
        require(!name.endsWith("/"))

        val resourceDir: String = if (baseDir.isEmpty()) {
            name
        } else {
            "$baseDir/$name"
        }
        val classLoader = LocateActionUiTests::class.java.classLoader
        val directoryStream = classLoader.getResourceAsStream(resourceDir)
        val subItems = if (directoryStream == null) {
            emptyList()
        } else {
            directoryStream.bufferedReader().use {
                it.readLines()
            }
        }
        val isFile = (subItems.isEmpty() && name.contains('.')) ||
                (subItems.isNotEmpty() && classLoader.getResource("$resourceDir/${subItems.first()}") == null)
        if (isFile) {
            println("Extracting $resourceDir to $basePath")
            PathUtils.copyFile(
                classLoader.getResource(resourceDir),
                basePath
            )
        } else {
            println("Descending into '$resourceDir' dir and creating '$basePath'")
            Files.createDirectories(basePath)
            subItems.forEach { subItemName ->
                extractResourceRecursively(resourceDir, subItemName, basePath.resolve(subItemName))
            }
        }
    }
}