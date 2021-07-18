package com.github.tarcv.surveyoridea.gui

import com.github.tarcv.testingteam.surveyor.Evaluator
import com.github.tarcv.testingteam.surveyor.Node
import com.intellij.codeInsight.daemon.impl.analysis.JavaModuleGraphUtil
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modifyModules
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.wm.ToolWindow
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.JavaCodeFragmentFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.EditorTextField
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke


class LocateToolWindow(private val project: Project, toolWindow: ToolWindow) {
    private lateinit var content: JPanel
    private lateinit var locatorField: EditorTextField

    fun createUIComponents() {
        val module = project.modifyModules {
            val module = newNonPersistentModule(
                "(Surveyor - UI Automator support)",
                ModuleTypeId.JAVA_MODULE
            )

            module
        }

        ModuleRootModificationUtil.updateModel(module) { model ->
            val library = model.moduleLibraryTable.createLibrary("uiautomator")
            library.modifiableModel.apply {
                addJarDirectory(
                    "file://C:/Users/const/.gradle/caches/transforms-3/7d370af9595cf4acefb849f695fd5ecb/transformed",
                    true
                )

                ApplicationManager.getApplication().invokeAndWait {
                    WriteAction.run<RuntimeException> { commit() }
                }
            }
        }

        val modulePsi = JavaModuleGraphUtil.findDescriptorByModule(module, false)

        val editorCode = JavaCodeFragmentFactory.getInstance(project).createCodeBlockCodeFragment(
            "new UiSelector()",
            modulePsi,
            true).apply {
            addImportsFromString(
                "androidx.test.uiautomator.UiSelector,androidx.test.uiautomator.By"
            )
        }
        val editorDocument = PsiDocumentManager.getInstance(project).getDocument(editorCode)!!
        val editorField = EditorTextField(editorDocument, project, JavaFileType.INSTANCE)

        editorField.registerKeyboardAction(
            object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent?) {
                    evaluate(editorCode)
                }
            },
            "evaluate",
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK, true),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        locatorField = editorField
    }

    private fun evaluate(editorCode: JavaCodeFragment) {
        val code = editorCode.importsToString()
            .split(Regex("""\s*,\s*"""))
            .joinToString("", postfix = editorCode.text) { "import $it; " }
        val result = Evaluator().evaluate(
            Node(null, emptyMap(), emptyList(), true),
            code
        )
    }

    fun getContent(): JPanel = content
}