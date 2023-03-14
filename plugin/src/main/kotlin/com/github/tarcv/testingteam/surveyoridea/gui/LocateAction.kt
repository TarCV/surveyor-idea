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

import com.github.tarcv.testingteam.surveyor.*
import com.github.tarcv.testingteam.surveyoridea.filetypes.XmlFileType
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.intellij.lang.xml.XMLLanguage
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import java.util.IdentityHashMap
import com.intellij.openapi.diagnostic.Logger as IdeaLogger

class LocateAction: AnAction() {
    companion object {
        private val logger = IdeaLogger.getInstance(LocateAction::class.java)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = getEventProject(e)
        val service = project?.getService(LocateToolHoldingService::class.java) ?: return
        val locator = service.getCurrentLocator() ?: return
        val locatorType = service.locatorType ?: return project.notify(
            "Can't locate an element when no locator type is selected",
            NotificationType.ERROR
        )

        // TODO: Check if locatorType supports current UI snapshot type
        val (editor, xmlFile: PsiFile) = FileEditorManager.getInstance(project).selectedEditors
            .asSequence()
            .mapNotNull { editor ->
                val virtualFile = editor.file
                val psiFile = virtualFile?.let { it1 -> PsiManager.getInstance(project).findFile(it1) }
                val xmlFile = psiFile?.viewProvider?.getPsi(XMLLanguage.INSTANCE) as? XmlFile
                xmlFile?.let {
                    editor to it
                }
            }
            .firstOrNull()
            ?: return project.notify(
                "Can't locate an element when no UI Automator snapshot is focussed",
                NotificationType.ERROR
            )

        val mapping = IdentityHashMap<Node, UiPsiElementReference>()
        val nodes = XmlFileType::class.sealedSubclasses
            .firstNotNullOfOrNull { type ->
                requireNotNull(type.objectInstance)
                    .tryConvert(project, xmlFile, mapping)
            }
            ?.takeIf { it.isNotEmpty() }
            ?: return project.notify(
                "File in the active editor is empty or not of a known snapshot type",
                NotificationType.ERROR
            )

        val evaluateResult = try {
            Logger.apply {
                onDebugMessage = logger::debug
                onInfoMessage = logger::info
            }

            locatorType.evaluate(nodes, locator)
                ?: return project.notify(
                    "No elements were found",
                    NotificationType.WARNING
                )
        } catch (e: InvalidLocatorException) {
            return project.notify(
                "There are some mistakes in the locator: ${e.message}",
                NotificationType.WARNING
            )
        } catch (e: InvalidSnapshotException) {
            return project.notify(
                "There are some issues with the snapshot: ${e.message}",
                NotificationType.WARNING
            )
        }

        val psiNode = evaluateResult
            .let { mapping[it] }
            ?.psiElement
            ?: return project.notify(
                "Internal error when selecting an element found",
                NotificationType.ERROR
            )

        project.notify("Found an element", NotificationType.INFORMATION)
        with((editor as TextEditor).editor) {
            caretModel.moveToOffset(psiNode.textOffset, false)
            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }
}