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

import com.github.tarcv.testingteam.surveyoridea.data.LocatorType
import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.ui.SizedIcon
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.EmptyIcon
import javax.swing.Icon
import javax.swing.JComponent

class LocatorTypeComboBoxAction: ComboBoxAction(), DumbAware {
    override fun createPopupActionGroup(button: JComponent?): DefaultActionGroup {
        return DefaultActionGroup().apply {
            LocatorType::class.sealedSubclasses.forEach { kClass ->
                kClass.objectInstance?.let {
                    add(SelectLocatorTypeAction(it))
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        val project = getEventProject(e)
        val presentation = e.presentation
        presentation.isEnabled = run {
            val service = project?.getService(LocateToolHoldingService::class.java)
                ?: return@run false
            service.locatorType.let {
                if (it != null && !ActionPlaces.isMainMenuOrActionSearch(e.place)) {
                    presentation.text = it.title
                }
            }
            true
        }
    }
}

class SelectLocatorTypeAction(private val locatorType: LocatorType) : AnAction(), DumbAware {
    init {
        templatePresentation.apply {
            setText(locatorType.title, false)
            description = "Locate an element using ${locatorType.title}"
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        val project = getEventProject(e)
        val presentation = e.presentation
        presentation.isEnabled = run {
            val service = project?.getService(LocateToolHoldingService::class.java)
                ?: return@run false
            service.locatorType.let {
                if (locatorType == it) {
                    presentation.icon = scaleIconFrom(AllIcons.Actions.Checked)
                    presentation.selectedIcon = scaleIconFrom(AllIcons.Actions.Checked_selected)
                } else {
                    presentation.icon = EmptyIcon.ICON_16
                    presentation.selectedIcon = EmptyIcon.ICON_16
                }
            }
            true
        }
    }

    private fun scaleIconFrom(icon: Icon) = JBUIScale.scaleIcon(SizedIcon(icon, 16, 16))

    override fun actionPerformed(e: AnActionEvent) {
        val project = getEventProject(e)
        val service = project?.getService(LocateToolHoldingService::class.java) ?: return
        service.locatorType = locatorType
        // TODO: update textarea highlighting language
    }
}
