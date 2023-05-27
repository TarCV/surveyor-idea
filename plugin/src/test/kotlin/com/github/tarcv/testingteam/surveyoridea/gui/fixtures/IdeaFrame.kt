package com.github.tarcv.testingteam.surveyoridea.gui.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ActionButtonFixture
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.fixtures.JMenuBarFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.waitFor
import com.intellij.remoterobot.utils.waitForIgnoringError
import org.apache.commons.lang.StringEscapeUtils
import java.io.Serializable
import java.time.Duration

fun RemoteRobot.idea(function: IdeaFrame.() -> Unit) {
    find<IdeaFrame>(timeout = Duration.ofSeconds(10)).apply(function)
}

@FixtureName("Idea frame")
@DefaultXpath("IdeFrameImpl type", "//div[@class='IdeFrameImpl']")
class IdeaFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {
    val menuBar
        get() = step("Menu...") {
            return@step remoteRobot.find(JMenuBarFixture::class.java, JMenuBarFixture.byType())
        }

    val structureTree
        get() = step("With Structure tool window") {
            return@step jTree(byXpath("//div[@accessiblename='Structure View tree']"))
        }

    fun actionButtonByName(name: String): ActionButtonFixture {
        val escapedName = StringEscapeUtils.escapeJavaScript(name)
        return actionButton(byXpath("//div[@accessiblename='$escapedName']"))
    }

    fun waitForNoTasks(timeout: Duration = Duration.ofSeconds(10)) {
        waitFor(duration = timeout, interval = Duration.ofSeconds(1)) {
            find<ComponentFixture>(byXpath("//div[@class='InlineProgressPanel']"))
                .findAllText()
                .isEmpty()
        }
    }

    fun openFileInTestProject(filePath: String, editorKey: String) {
        val jsEscapedFilePath = StringEscapeUtils.escapeJavaScript(filePath)
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
                        const textEditor = FileEditorManagerEx.getInstance(project)
                            .openFile(file, true, true)
                            [0]
                        textEditor.editor.caretModel.moveToOffset(0)
                        local.put('$editorKey', textEditor)
                    }
                }))
                """.trimIndent()
        )
        waitForIgnoringError {
            0 == callJs<Int>(
                runInEdt = true, script =
                """
                        const textEditor = local.get('$editorKey')
                        textEditor.editor.caretModel.offset
                        """.trimIndent()
            )
        }
    }

    fun <T : Serializable> callJsInEditor(editorKey: String, code: String): T {
        @Suppress("JSUnusedLocalSymbols")
        return callJs(
            runInEdt = true, script =
            """const editor = local.get('$editorKey');$code"""
        )
    }

    fun getSelectedXmlNodeOuterXml(editorKey: String) =
        callJsInEditor<String>(editorKey, """
                                    const start = editor.editor.caretModel.currentCaret.selectionStart
                                    const end = editor.editor.caretModel.currentCaret.selectionEnd
                                    const snapshotText = editor.editor.document.text
                                    snapshotText.substring(snapshotText.lastIndexOf("<", start), snapshotText.indexOf(">", end) + 1)
                """.trimIndent()
        )
}