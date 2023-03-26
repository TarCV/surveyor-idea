package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.data.LocatorType
import com.github.tarcv.testingteam.surveyoridea.services.LocateToolHoldingService
import com.intellij.execution.actions.RunConfigurationsComboBoxAction
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
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
                    presentation.icon = RunConfigurationsComboBoxAction.CHECKED_ICON
                    presentation.selectedIcon = RunConfigurationsComboBoxAction.CHECKED_SELECTED_ICON
                } else {
                    presentation.icon = RunConfigurationsComboBoxAction.EMPTY_ICON
                    presentation.selectedIcon = RunConfigurationsComboBoxAction.EMPTY_ICON
                }
            }
            true
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = getEventProject(e)
        val service = project?.getService(LocateToolHoldingService::class.java) ?: return
        service.locatorType = locatorType
        // TODO: update textarea highlighting language
    }
}
