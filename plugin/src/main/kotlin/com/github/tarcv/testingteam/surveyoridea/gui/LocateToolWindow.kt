/*
 *  Copyright (C) 2023 TarCV
 *
 *  This file is part of UI Surveyor.
 *  UI Surveyor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.tarcv.testingteam.surveyoridea.gui

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiSelector
import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.intellij.codeInsight.daemon.impl.analysis.JavaModuleGraphUtil
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
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
import com.intellij.ui.EditorTextField
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke


class LocateToolWindow(private val project: Project, toolWindow: ToolWindow) {
    private lateinit var content: JPanel
    private lateinit var locatorFragment: JavaCodeFragment
    private lateinit var locatorField: JPanel
    private lateinit var toolbar: JComponent

    companion object {
        const val moduleName = "UI Surveyor (Highlighting)"
    }

    fun createUIComponents() {
        toolbar = with(ActionManager.getInstance()) {
            createActionToolbar(
                ActionPlaces.TOOLWINDOW_CONTENT,
                getAction("com.github.tarcv.testingteam.surveyoridea.gui.LocateToolWindow.toolbar") as ActionGroup,
                false
            ).component
        }

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
