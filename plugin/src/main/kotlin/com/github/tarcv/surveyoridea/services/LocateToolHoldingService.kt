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
package com.github.tarcv.surveyoridea.services

import com.github.tarcv.surveyoridea.gui.LocateToolWindow
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import javax.annotation.concurrent.GuardedBy

@Service
class LocateToolHoldingService(project: Project) {
    private val lock = Any()

    @GuardedBy("lock")
    private var locateToolWindow: LocateToolWindow? = null

    fun registerToolWindow(toolWindow: LocateToolWindow) = synchronized(lock) {
        locateToolWindow = toolWindow
    }

    fun getCurrentLocator(): String? = synchronized(lock) {
        return locateToolWindow?.getCurrentLocator()
    }
}
