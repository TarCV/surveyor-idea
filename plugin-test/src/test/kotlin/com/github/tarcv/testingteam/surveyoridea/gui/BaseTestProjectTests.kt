package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.LaunchIdeExtension
import com.github.tarcv.testingteam.surveyoridea.MethodVideoExtension
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.IdeaFrame
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.ifTipOfTheDayDialogPresent
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.JButtonFixture
import com.intellij.remoterobot.steps.CommonSteps
import com.intellij.remoterobot.utils.DefaultHttpClient
import com.intellij.remoterobot.utils.WaitForConditionTimeoutException
import com.intellij.remoterobot.utils.waitForIgnoringError
import org.apache.commons.io.file.PathUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import javax.imageio.ImageIO

@ExtendWith(MethodVideoExtension::class, LaunchIdeExtension::class)
open class BaseTestProjectTests {
    companion object {
        @JvmStatic
        protected val droidAutomatorSnapshotFile = """demo/snapshots/dump.uix"""
        @JvmStatic
        protected val droidAutomator23SnapshotFile = """demo/snapshots/dump23.uix"""

        @JvmStatic
        protected val iPredicateSnapshotFile = """demo/snapshots/main.xml"""

        @JvmStatic
        protected val editorWithSnapshot = "editorWithSnapshot"

        const val REQUIRES_SCREENSHOT_ASSUMPTIONS_TAG = "REQUIRES_SCREENSHOT_ASSUMPTIONS"

        fun checkScreenshotAssumptions(robot: RemoteRobot) {
            Assumptions.assumeTrue(robot.isLinux(), "Screenshot are only enabled on Linux")
        }
    }

    @field:TempDir
    lateinit var tempDir: Path

    lateinit var projectPath: Path

    protected val remoteRobot: RemoteRobot by lazy {
        val httpClient = DefaultHttpClient.client.newBuilder()
            .readTimeout(Duration.ofMinutes(1)) // prevent timing out on Macs when entering paths in Open dialogs
            .build()
        RemoteRobot(
            "http://127.0.0.1:8082",
            httpClient
        ).apply {
            waitForIgnoringError(Duration.ofMinutes(1), Duration.ofSeconds(5)) {
                callJs("true")
            }
        }
    }
    protected val commonSteps by lazy { CommonSteps(remoteRobot) }

    @BeforeEach
    fun openTestProject(testInfo: TestInfo) {
        if (REQUIRES_SCREENSHOT_ASSUMPTIONS_TAG in testInfo.tags) {
            checkScreenshotAssumptions(remoteRobot)
        }

        PathUtils.cleanDirectory(tempDir)

        projectPath = tempDir.resolve("project")
        extractResourceRecursively("", "project", projectPath)

        with(remoteRobot) {
            commonSteps.openProject(projectPath.toAbsolutePath().toString())

            idea {
                ifTipOfTheDayDialogPresent {
                    close()
                }
                closeCodeWithMeBubbleIfNeeded()
                commonSteps.waitForSmartMode(1)

                if (REQUIRES_SCREENSHOT_ASSUMPTIONS_TAG in testInfo.tags) {
                    resizeWindow(1024, 768)
                }
            }
        }
    }

    private fun IdeaFrame.closeCodeWithMeBubbleIfNeeded() {
        try {
            find<JButtonFixture>(JButtonFixture.byText("Got It"))
                .click()
        } catch (e: WaitForConditionTimeoutException) {
            // no tooltip displayed, so nothing to do
        }
    }

    @AfterEach
    fun closeTestProject(testInfo: TestInfo): Unit = with(remoteRobot) {
        kotlin.runCatching {
            takeAndWriteScreenshot(testInfo)
        }
        kotlin.runCatching {
            commonSteps.closeProject()
            sleep(3_000) // wait until invokeAction is executed
        }
        kotlin.runCatching {
            commonSteps.invokeAction("CloseAllProjects")
            sleep(3_000) // wait until invokeAction is executed
        }
    }

    protected fun relativeToProject(path: String): Path {
        return projectPath.resolve(path)
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
                (subItems.isNotEmpty() && classLoader.getResource("$resourceDir/${subItems.first()}") === null)
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