/*
 *  Copyright (C) 2024 TarCV
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
import com.intellij.codeInsight.daemon.impl.analysis.JavaModuleGraphUtil
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modifyModules
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.JavaCodeFragmentFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorTextField
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

@Suppress("UnstableApiUsage", "unused") // Used by string reference in LocateToolWindowFactory.createToolWindowContent
class JvmLocateToolWindow(project: Project) : LocateToolWindow(project) {
    companion object {
        const val OLD_MODULE_NAME = "UISurveyor_Highlighting"
        const val MODULE_NAME = "__UISurveyor_Highlighting"
        const val HIGHLIGHTING_LIBRARY_NAME = "uiautomator"
    }

    private val locatorFragment: PsiFile?
        get() {
            val docField = locatorField as EditorTextField
            return PsiDocumentManager.getInstance(project).getPsiFile(docField.document)
        }

    override val fileType: LanguageFileType = JavaFileType.INSTANCE

    override fun initSelectorField(editorField: EditorTextField) {
        invokeLater {
            removeModuleIfExists(OLD_MODULE_NAME)
            removeModuleIfExists(MODULE_NAME)
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
    }

    private fun removeModuleIfExists(name: String) {
        val module = ModuleManager.getInstance(project).findModuleByName(name)
            ?: return
        project.modifyModules {
            var isHighlightingModule = false
            ModuleRootModificationUtil.modifyModel(module) { model ->
                isHighlightingModule = model.moduleLibraryTable.libraries
                    .singleOrNull()
                    ?.name == HIGHLIGHTING_LIBRARY_NAME
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
                MODULE_NAME,
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
                    .createLibrary(HIGHLIGHTING_LIBRARY_NAME)
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

    override fun getCurrentLocator(): String {
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