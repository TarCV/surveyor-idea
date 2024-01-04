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

import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.playback.commands.ActionCommand
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.EditorTextField
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke


abstract class LocateToolWindow(protected val project: Project) {
    private lateinit var content: JPanel
    protected lateinit var locatorField: JPanel
    private lateinit var toolbar: JComponent

    protected abstract val fileType: LanguageFileType

    fun createUIComponents() {
        val actionToolbar = with(ActionManager.getInstance()) {
            createActionToolbar(
                ActionPlaces.TOOLWINDOW_CONTENT,
                getAction("com.github.tarcv.testingteam.surveyoridea.gui.LocateToolWindow.toolbar") as ActionGroup,
                false
            )
        }
        toolbar = actionToolbar.component

        val editorField = EditorTextField("new UiSelector()", project, fileType)
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

        initSelectorField(editorField)

        project.getService(LocateToolHoldingService::class.java).registerToolWindow(this)
        actionToolbar.setTargetComponent(editorField)
        locatorField = editorField
    }

    protected abstract fun initSelectorField(editorField: EditorTextField)

    fun getContent(): JPanel = content

    abstract fun getCurrentLocator(): String
}
