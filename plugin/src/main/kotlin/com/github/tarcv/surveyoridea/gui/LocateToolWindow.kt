package com.github.tarcv.surveyoridea.gui

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiSelector
import com.github.tarcv.surveyoridea.filetypes.uix.Hierarchy
import com.github.tarcv.surveyoridea.services.LocateToolHoldingService
import com.github.tarcv.testingteam.surveyor.Evaluator
import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyor.Properties
import com.intellij.codeInsight.daemon.impl.analysis.JavaModuleGraphUtil
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modifyModules
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.ui.playback.commands.ActionCommand
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.wm.ToolWindow
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.JavaCodeFragmentFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.ui.EditorTextField
import com.intellij.util.xml.DomManager
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.util.IdentityHashMap
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke


class LocateToolWindow(private val project: Project, toolWindow: ToolWindow) {
    private lateinit var content: JPanel
    private lateinit var locatorFragment: JavaCodeFragment
    private lateinit var locatorField: EditorTextField

    companion object {
        const val moduleName = "UI Surveyor (Highlighting)"
    }

    fun createUIComponents() {
        val module = ModuleManager.getInstance(project).findModuleByName(moduleName)
            ?: createModuleForHighlighting()

        val modulePsi = JavaModuleGraphUtil.findDescriptorByModule(module, false)

        val importedClasses = listOf(UiSelector::class.java, By::class.java)
            .joinToString(",") { it.name }
        val editorCode = JavaCodeFragmentFactory.getInstance(project).createExpressionCodeFragment(
            "new UiSelector()",
            modulePsi,
            null,
            true
        ).apply {
            addImportsFromString(importedClasses)
        }
        val editorDocument = PsiDocumentManager.getInstance(project).getDocument(editorCode)!!
        val editorField = EditorTextField(editorDocument, project, JavaFileType.INSTANCE)

        editorField.registerKeyboardAction(
            object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent?) {
                    val actionManager = ActionManager.getInstance()
                    val actionId = LocateAction::class.java.name
                    val action = actionManager.getAction(actionId) ?: return // TODO
                    actionManager.tryToExecute(
                        action,
                        ActionCommand.getInputEvent(actionId),
                        null,
                        ActionPlaces.TOOLWINDOW_CONTENT,
                        true
                    )
                }
            },
            "evaluate",
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK, true),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        project.getService(LocateToolHoldingService::class.java).registerToolWindow(this)
        locatorFragment = editorCode
        locatorField = editorField
    }

    private fun createModuleForHighlighting(): Module {
        return project.modifyModules {
            val modulePath = Paths.get(project.projectFilePath!!).parent.resolve("$moduleName.iml")
            val module = newModule(
                modulePath.toString(),
                ModuleTypeId.JAVA_MODULE
            )

            val automatorClass = UiSelector::class.java
            ModuleRootModificationUtil.updateModel(module) { model ->
                val automatorJarFile = getAutomatorJarPath(automatorClass)
                val library = model.moduleLibraryTable.createLibrary("uiautomator")
                library.modifiableModel.apply {
                    addRoot(
                        VfsUtil.getUrlForLibraryRoot(automatorJarFile.toFile()),
                        OrderRootType.CLASSES
                    )

                    ApplicationManager.getApplication().invokeAndWait {
                        WriteAction.run<RuntimeException> { commit() }
                    }
                }
            }
            module
        }
    }

    private fun getAutomatorJarPath(automatorClass: Class<UiSelector>): Path {
        val classPath = automatorClass.name.replace('.', '/') + ".class"
        val classUrl = automatorClass.classLoader.getResource(classPath)!!.toExternalForm()
        val jarUrl = classUrl
            .removeSuffix(classPath)
            .removeSuffix("/")
            .removeSuffix("!")
            .replaceFirst(Regex("^.*(?=file:)"), "")
        return Paths.get(URI(jarUrl))
    }

    fun getContent(): JPanel = content

    fun getCurrentLocator(): String {
        return locatorFragment.importsToString()
            .split(Regex("""\s*,\s*"""))
            .joinToString("", postfix = locatorFragment.text) { "import $it; " }
    }
}
class LocateAction: AnAction() {
    override fun update(e: AnActionEvent) {
        super.update(e)
        // TODO: check if an XML is selected
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = getEventProject(e)
        val service = project?.getService(LocateToolHoldingService::class.java) ?: return
        val locator = service.getCurrentLocator() ?: return

        val (editor, xmlFile) = FileEditorManager.getInstance(project).selectedEditors
            .asSequence()
            .mapNotNull { editor ->
                val virtualFile = editor.file
                val psiFile = virtualFile?.let { it1 -> PsiManager.getInstance(project).findFile(it1) }
                val xmlFile = psiFile?.viewProvider?.getPsi(XMLLanguage.INSTANCE) as? XmlFile
                xmlFile?.let {
                    editor to it
                }
            }
            .firstOrNull() ?: return // TODO

        val uix = DomManager.getDomManager(project).getFileElement(xmlFile, Hierarchy::class.java) ?: return // TODO
        val mapping = IdentityHashMap<Node, com.github.tarcv.surveyoridea.filetypes.uix.Node>()
        val nodes = uix.rootElement.nodes
            .map { convert(it, mapping) }

        val rootNode = when {
            nodes.size > 1 -> Node(null, emptyMap(), nodes, true)
            nodes.size == 1 -> nodes[0]
            else -> return // TODO
        }

        val psiNode = Evaluator()
            .evaluate(rootNode, locator)
            .let { mapping[it] }
            ?.xmlElement
            ?: return // TODO

        with((editor as TextEditor).editor) {
            caretModel.moveToOffset(psiNode.textOffset, false)
            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }

    private fun convert(
        node: com.github.tarcv.surveyoridea.filetypes.uix.Node,
        mapping: MutableMap<Node, com.github.tarcv.surveyoridea.filetypes.uix.Node>
    ): Node {
        val props: Map<Properties<*>, Any?> = listOf(
            Properties.IS_CHECKABLE to node.checkable.value,
            Properties.IS_CHECKED to node.checked.value,
            Properties.CLASS_NAME to node.`clazz`.value,
            Properties.IS_CLICKABLE to node.clickable.value,
            Properties.ACCESSIBILITY_DESCRIPTION to node.contentDesc.value,
            Properties.IS_ENABLED to node.enabled.value,
            Properties.IS_FOCUSABLE to node.focusable.value,
            Properties.IS_FOCUSED to node.focused.value,
            Properties.IS_LONG_CLICKABLE to node.longClickable.value,
            Properties.PACKAGE_NAME to node.`package`.value,
            Properties.IS_PASSWORD_FIELD to node.password.value,
            Properties.RESOURCE_ID to node.resourceId.value,
            Properties.IS_SCROLLABLE to node.scrollable.value,
            Properties.IS_SELECTED to node.selected.value,
            Properties.TEXT to node.text.value,
        ).filter { it.second != null }.toMap()

        val out = Node(
            null,
            props,
            node.nodes.map { convert(it, mapping) },
            true // TODO
        ).apply {
            finalizeChildren()
        }

        mapping[out] = node

        return out
    }
}
