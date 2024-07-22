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

import com.github.tarcv.testingteam.surveyoridea.services.LocatorTypeChangedListener
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class LocateToolWindowFactory : ToolWindowFactory {
    private val contentFactory = ContentFactory.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val locateToolWindow: LocateToolWindow = if (hasJavaPlugin()) {
            Class.forName("com.github.tarcv.testingteam.surveyoridea.gui.JvmLocateToolWindow")
                .getConstructor(Project::class.java)
                .newInstance(project) as LocateToolWindow
        } else {
            SimpleLocateToolWindow(project)
        }
        project.messageBus.connect().subscribe(LocatorTypeChangedListener.topic, locateToolWindow)
        val content = contentFactory.createContent(locateToolWindow, null, false).apply {
            preferredFocusableComponent = locateToolWindow.preferredFocusedComponent
        }
        toolWindow.contentManager.addContent(content)
    }

    private fun hasJavaPlugin(): Boolean {
        return try {
            Class.forName("com.intellij.psi.JavaCodeFragment", false, javaClass.classLoader)
            true
        } catch (e: ReflectiveOperationException) {
            false
        }
    }
}