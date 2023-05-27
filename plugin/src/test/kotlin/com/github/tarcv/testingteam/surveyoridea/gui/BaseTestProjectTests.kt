package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.ifTipOfTheDayDialogPresent
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.utils.waitForIgnoringError
import com.intellij.util.TimeoutUtil.sleep
import org.apache.commons.io.file.PathUtils
import org.junit.Assume
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.net.ConnectException
import java.nio.file.Files
import java.nio.file.Path

open class BaseTestProjectTests {
    @field:TempDir
    lateinit var tempDir: Path

    lateinit var projectPath: Path

    protected val droidAutomatorSnapshotFile by lazy { """$projectPath/demo/snapshots/dump.uix""" }

    protected val remoteRobot: RemoteRobot by lazy {
        try {
            RemoteRobot("http://127.0.0.1:8082").apply {
                runJs("")
            }
        } catch (e: ConnectException) {
            Assume.assumeNoException("IDE should be running in UI tests mode", e)
            throw e
        }
    }

    @BeforeEach
    fun openTestProject() {
        PathUtils.cleanDirectory(tempDir)

        projectPath = tempDir.resolve("project")
        extractResourceRecursively("", "project", projectPath)

        with(remoteRobot) {
            runJs(
                runInEdt = true, script =
                """
                importPackage(java.nio.file)
                importPackage(com.intellij.openapi.application)
                importPackage(com.intellij.ide.impl)
                ApplicationManager.getApplication().invokeLater(new Runnable({
                    run: function () {
                        ProjectUtil.openOrImport(Paths.get("$projectPath"))
                    }
                }))
            """.trimIndent()
            )
            waitForIgnoringError {
                callJs(
                    runInEdt = true, script =
                    """
                    importPackage(com.intellij.openapi.project.ex)
                    ProjectManagerEx.getInstance().openProjects.length > 0
                """.trimIndent()
                )
            }
            sleep(2_000)

            idea {
                ifTipOfTheDayDialogPresent() {
                    close()
                }
                waitForNoTasks()
            }
        }
    }

    @AfterEach
    fun closeTestProject(): Unit = with(remoteRobot) {
        runJs(
            runInEdt = true, script =
            """
                    importPackage(com.intellij.openapi.project.ex)
                    importPackage(com.intellij.openapi.application)
                    ApplicationManager.getApplication().invokeLater(() => { // only invokeLater is safe for closeAndDisposeAllProjects
                        ProjectManagerEx.getInstanceEx().closeAndDisposeAllProjects(/* checkCanClose = */ false)
                    })
                """
        )
        sleep(3_000) // wait until invokeLater lambda is executed
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