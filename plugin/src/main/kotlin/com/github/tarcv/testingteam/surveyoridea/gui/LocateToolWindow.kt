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
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modifyModules
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.playback.commands.ActionCommand
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.JavaCodeFragmentFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorTextField
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke


@Suppress("UnstableApiUsage")
class LocateToolWindow(private val project: Project) {
    private lateinit var content: JPanel
    private val locatorFragment: PsiFile?
        get() {
            val docField = locatorField as EditorTextField
            return PsiDocumentManager.getInstance(project).getPsiFile(docField.document)
        }
    private lateinit var locatorField: JPanel
    private lateinit var toolbar: JComponent

    companion object {
        const val oldModuleName = "UISurveyor_Highlighting"
        const val moduleName = "__UISurveyor_Highlighting"
        const val highlightingLibraryName = "uiautomator"
    }

    fun createUIComponents() {
        val actionToolbar = with(ActionManager.getInstance()) {
            createActionToolbar(
                ActionPlaces.TOOLWINDOW_CONTENT,
                getAction("com.github.tarcv.testingteam.surveyoridea.gui.LocateToolWindow.toolbar") as ActionGroup,
                false
            )
        }
        toolbar = actionToolbar.component

        val editorField = EditorTextField("new UiSelector()", project, JavaFileType.INSTANCE)
        val locateFromKeyboardAction = object : AnAction("Evaluate") {
            override fun actionPerformed(e: AnActionEvent) {
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
        }
        locateFromKeyboardAction.registerCustomShortcutSet(
            CustomShortcutSet(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER,
                if (SystemInfo.isMac) { InputEvent.META_DOWN_MASK } else { InputEvent.CTRL_DOWN_MASK },
                true
            )),
            editorField
        )

        invokeLater {
            removeModuleIfExists(oldModuleName)
            removeModuleIfExists(moduleName)
            val module = createModuleForHighlighting()

            val modulePsi = JavaModuleGraphUtil.findDescriptorByModule(module, false)

            val importedClasses = listOf(UiSelector::class.java, By::class.java)
                .joinToString(",") { it.name }
            val editorCode = JavaCodeFragmentFactory.getInstance(project).createExpressionCodeFragment(
                editorField.text,
                modulePsi,
                null,
                true
            ).apply {
                addImportsFromString(importedClasses)
            }

            editorField.document = PsiDocumentManager.getInstance(project).getDocument(editorCode)!!
        }

        project.getService(LocateToolHoldingService::class.java).registerToolWindow(this)
        actionToolbar.setTargetComponent(editorField)
        locatorField = editorField
    }

    private fun removeModuleIfExists(name: String) {
        val module = ModuleManager.getInstance(project).findModuleByName(name)
            ?: return
        project.modifyModules {
            var isHighlightingModule = false
            ModuleRootModificationUtil.modifyModel(module) { model ->
                isHighlightingModule = model.moduleLibraryTable.libraries
                    .singleOrNull()
                    ?.name == highlightingLibraryName
                false
            }
            if (isHighlightingModule) {
                disposeModule(module)
            }
        }
    }

    private fun createModuleForHighlighting(): Module {
        val module: Module = project.modifyModules {
            newNonPersistentModule(
                moduleName,
                ModuleTypeId.JAVA_MODULE
            )
        }

        project.modifyModules {
            ModuleRootModificationUtil.updateModel(module) { model ->
                val projectSdk = ProjectRootManager.getInstance(project).projectSdk
                if (projectSdk == null || projectSdk.sdkType is JavaSdkType) {
                    model.inheritSdk()
                }
            }
        }

        project.modifyModules {
            ModuleRootModificationUtil.updateModel(module) { model ->
                val automatorClass = UiSelector::class.java
                val automatorJarFile = getAutomatorJarPath(automatorClass)
                model.moduleLibraryTable
                    .createLibrary(highlightingLibraryName)
                    .modifiableModel.apply {
                        addRoot(
                            VfsUtil.getUrlForLibraryRoot(automatorJarFile.toFile()),
                            OrderRootType.CLASSES
                        )

                        commit()
                    }
            }
            module
        }
        return module
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
        val fragment = locatorFragment
        val imports = if (fragment is JavaCodeFragment) {
            fragment.importsToString()
                .split(Regex("""\s*,\s*"""))
                .joinToString("") { "import $it; " }
        } else {
            ""
        }
        return imports + (fragment?.text ?: "")
    }
}
