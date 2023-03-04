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

import com.github.tarcv.testingteam.surveyor.*
import com.github.tarcv.testingteam.surveyor.Properties
import com.github.tarcv.testingteam.surveyoridea.filetypes.uix.Hierarchy
import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.intellij.lang.xml.XMLLanguage
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomManager
import java.util.*
import com.intellij.openapi.diagnostic.Logger as IdeaLogger

class LocateAction: AnAction() {
    companion object {
        private val logger = IdeaLogger.getInstance(LocateAction::class.java)
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
            .firstOrNull()
            ?: return project.notify(
                "Can't locate an element when no UI Automator dump is focussed",
                NotificationType.ERROR
            )

        val uix = DomManager.getDomManager(project).getFileElement(xmlFile, Hierarchy::class.java)
            ?: return project.notify(
                "Can't locate an element when no UI Automator dump is focussed",
                NotificationType.ERROR
            )

        val mapping = IdentityHashMap<Node, com.github.tarcv.testingteam.surveyoridea.filetypes.uix.Node>()
        val nodes = uix.rootElement.nodes
            .map { convert(it, mapping) }

        val rootNode = when {
            nodes.size > 1 -> Node(null, emptyMap(), nodes, true)
            nodes.size == 1 -> nodes[0]
            else -> return project.notify(
                "Can't locate an element when the focussed dump has multiple root nodes",
                NotificationType.ERROR
            )
        }

        val evaluateResult = try {
            Logger.apply {
                onDebugMessage = logger::debug
                onInfoMessage = logger::info
            }

            Evaluator().evaluate(rootNode, locator)
                ?: return project.notify(
                    "No elements were found",
                    NotificationType.WARNING
                )
        } catch (e: InvalidLocatorException) {
            return project.notify(
                "There are some mistakes in the locator",
                NotificationType.WARNING
            )
        }

        val psiNode = evaluateResult
            .let { mapping[it] }
            ?.xmlElement
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

    private fun convert(
        node: com.github.tarcv.testingteam.surveyoridea.filetypes.uix.Node,
        mapping: MutableMap<Node, com.github.tarcv.testingteam.surveyoridea.filetypes.uix.Node>
    ): Node {
        val props: Map<Properties<*>, Any?> = listOf(
            Properties.IS_CHECKABLE to node.checkable.value,
            Properties.IS_CHECKED to node.checked.value,
            Properties.CLASS_NAME to node.clazz.value,
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