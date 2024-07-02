package com.github.tarcv.testingteam.surveyoridea.gui.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ActionButtonFixture
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.fixtures.JMenuBarFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.steps.CommonSteps
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.attempt
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitForIgnoringError
import org.apache.commons.text.StringEscapeUtils
import java.io.Serializable
import java.nio.file.Path
import java.time.Duration

fun RemoteRobot.idea(function: IdeaFrame.() -> Unit) {
    find<IdeaFrame>(timeout = Duration.ofSeconds(10)).apply(function)
}

@FixtureName("Idea frame")
@DefaultXpath("IdeFrameImpl type", "//div[@class='IdeFrameImpl']")
class IdeaFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {
    private val menuBar
        get() = step("Menu...") {
            return@step remoteRobot.find(
                JMenuBarFixture::class.java, JMenuBarFixture.byType(), Duration.ofSeconds(10)
            )
        }

    val structureTree
        get() = step("With Structure tool window") {
            return@step jTree(
                byXpath("//div[@accessiblename='Structure View tree']"),
                Duration.ofSeconds(10)
            )
        }

    fun selectInMenuBar(vararg items: String) {
        return attempt(5) {
            try {
                return@attempt menuBar.select(*items)
            } catch (e: Throwable) {
                keyboard { escape() }
                throw e
            }
        }
    }

    fun actionButtonByName(name: String): ActionButtonFixture {
        val escapedName = StringEscapeUtils.escapeEcmaScript(name)
        return actionButton(
            byXpath("//div[@accessiblename='$escapedName']"),
            Duration.ofSeconds(10)
        )
    }

    fun RemoteRobot.openFileAndToolWindow(filePath: Path, editorKey: String) {
        idea {
            openFileInTestProject(filePath, editorKey)
            openToolWindow()
        }
    }

    fun openFileInTestProject(filePath: Path, editorKey: String) {
        val jsEscapedFilePath = StringEscapeUtils.escapeEcmaScript(filePath.toString())
        println("Opening '$jsEscapedFilePath'")
        runJs(
            runInEdt = true, script =
            """
                importPackage(java.nio.file)
                importPackage(com.intellij.openapi.application)
                importPackage(com.intellij.openapi.project.ex)
                importPackage(com.intellij.openapi.fileEditor)
                importPackage(com.intellij.openapi.fileEditor.ex)
                importPackage(com.intellij.openapi.vfs)
                
                ApplicationManager.getApplication().invokeLater(new Runnable({
                    run: function () {
                        const project = ProjectManagerEx.getInstance().openProjects[0]
                        const file = LocalFileSystem.getInstance().findFileByNioFile(Paths.get("$jsEscapedFilePath"))
                        const textEditor = FileEditorManagerEx.getInstanceEx(project)
                            .openFile(file, true, true)
                            [0]
                        global.put('$editorKey', textEditor)
                    }
                }))
                """.trimIndent()
        )
        runJs(
            runInEdt = true, script =
            """
                importPackage(com.intellij.openapi.application)
                importPackage(com.intellij.openapi.project.ex)
                importPackage(com.intellij.openapi.wm)

                ApplicationManager.getApplication().invokeLater(new Runnable({
                    run: function () {
                        const project = ProjectManagerEx.getInstance().openProjects[0]
                        const textEditor = global.get('$editorKey')
                        if (textEditor != null && !textEditor.editor.isDisposed()) {
                            IdeFocusManager.getInstance(project).requestFocus(textEditor.editor.contentComponent, true);
                        }
                    }
                }))
                """.trimIndent()
        )
        runJs(
            runInEdt = true, script =
            """
                importPackage(com.intellij.openapi.application)
                ApplicationManager.getApplication().invokeLater(new Runnable({
                    run: function () {
                        const textEditor = global.get('$editorKey')
                        if (textEditor != null && !textEditor.editor.isDisposed()) {
                            textEditor.editor.caretModel.moveToOffset(0)
                        }
                    }
                }))
                """.trimIndent()
        )
        waitForIgnoringError(Duration.ofMinutes(1)) {
            0 == callJs<Int>(
                runInEdt = true, script =
                """
                        const textEditor = global.get('$editorKey')
                        if (textEditor != null && !textEditor.editor.isDisposed()) {
                          textEditor.editor.caretModel.offset
                        } else {
                          -1
                        }
                        """.trimIndent()
            )
        }
    }

    fun resizeWindow(newWidth: Int, newHeight: Int) {
        runJs(
            runInEdt = true, script =
            """
                    importPackage(com.intellij.openapi.application)
                    ApplicationManager.getApplication().invokeLater(new Runnable({
                        run: function () {
                            component.setSize($newWidth, $newHeight)
                        }
                    }))
                          """.trimIndent()
        )
    }

    private fun RemoteRobot.openToolWindow() {
        CommonSteps(this)
            .invokeAction("ActivateLocateElementToolWindow")
        waitForToolWindowSmartMode()
    }

    private fun waitForToolWindowSmartMode() {
        // Opening 'Locate Element' tool window sometimes causes reindexing
        Thread.sleep(2_000)
        CommonSteps(remoteRobot).waitForSmartMode(1)
    }

    fun <T : Serializable> callJsInEditor(editorKey: String, code: String): T {
        @Suppress("JSUnusedLocalSymbols")
        return callJs(
            runInEdt = true, script =
            """const editor = global.get('$editorKey');$code"""
        )
    }

    fun getSelectedXmlNodeOuterXml(editorKey: String) =
        callJsInEditor<String>(editorKey, """
                                    if (editor == null || editor.editor.isDisposed()) {
                                        "<Couldn't get editor instance>"
                                    } else {
                                        const start = editor.editor.caretModel.currentCaret.selectionStart
                                        const end = editor.editor.caretModel.currentCaret.selectionEnd
                                        const snapshotText = editor.editor.document.text
                                        snapshotText.substring(snapshotText.lastIndexOf("<", start), snapshotText.indexOf(">", end) + 1)
                                    }
                """.trimIndent()
        )
}