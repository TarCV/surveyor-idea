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

import com.github.tarcv.testingteam.surveyoridea.data.DroidUiSelectorLocatorType
import com.github.tarcv.testingteam.surveyoridea.data.IClassChainLocatorType
import com.github.tarcv.testingteam.surveyoridea.data.IPredicateLocatorType
import com.github.tarcv.testingteam.surveyoridea.data.LocatorType
import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.github.tarcv.testingteam.surveyoridea.services.LocatorTypeChangedListener
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.playback.commands.ActionCommand
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.EditorTextField
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke


abstract class LocateToolWindow(protected val project: Project) : LocatorTypeChangedListener {
    private lateinit var content: JPanel
    protected lateinit var locatorField: JPanel
    private lateinit var toolbar: JComponent

    protected abstract val fileType: LanguageFileType
    private var locatorProvider: () -> String = { "" }

    init {
        project.messageBus.connect().subscribe(LocatorTypeChangedListener.topic, this)
    }

    fun createUIComponents() {
        val actionToolbar = with(ActionManager.getInstance()) {
            createActionToolbar(
                ActionPlaces.TOOLWINDOW_CONTENT,
                getAction("com.github.tarcv.testingteam.surveyoridea.gui.LocateToolWindow.toolbar") as ActionGroup,
                true
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

        editorField.setOneLineMode(false)

        with(project.getService(LocateToolHoldingService::class.java)) {
            onLocatorTypeChanged(locatorType)
            registerToolWindow(this@LocateToolWindow)
        }
        actionToolbar.setTargetComponent(editorField)
        locatorField = editorField
    }

    override fun onLocatorTypeChanged(newType: LocatorType?): Unit = invokeLater {
        val editorField = locatorField as EditorTextField
        editorField.isEnabled = false
        try {
            when (newType) {
                DroidUiSelectorLocatorType, null -> {
                    switchToDroidUiAutomator(editorField)
                    locatorProvider = ::getCurrentDroidUiAutomatorLocator
                }
                IClassChainLocatorType, IPredicateLocatorType -> {
                    with(editorField) {
                        document = EditorFactory.getInstance().createDocument(StringUtil.convertLineSeparators(text))
                        fileType = FileTypes.PLAIN_TEXT
                    }
                    locatorProvider = ::getPlainTextLocatorText
                }
            }
        } finally {
            editorField.isEnabled = true
        }
    }

    protected fun getPlainTextLocatorText(): String {
        val docField = locatorField as EditorTextField
        @Suppress("USELESS_ELVIS")
        return PsiDocumentManager.getInstance(project).getPsiFile(docField.document)
            ?.text
            ?: docField.text
            ?: ""
    }

    fun getCurrentLocator(): String = locatorProvider()

    abstract fun switchToDroidUiAutomator(editorField: EditorTextField)

    fun getContent(): JPanel = content

    abstract fun getCurrentDroidUiAutomatorLocator(): String
}
