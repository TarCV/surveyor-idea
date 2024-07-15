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
package com.github.tarcv.testingteam.surveyoridea.services

import com.github.tarcv.testingteam.surveyoridea.data.DroidUiSelectorLocatorType
import com.github.tarcv.testingteam.surveyoridea.data.LocatorType
import com.github.tarcv.testingteam.surveyoridea.gui.LocateToolWindow
import com.google.errorprone.annotations.concurrent.GuardedBy
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.ui.AppUIUtil

// TODO: implement PersistentStateComponent
@Service(Service.Level.PROJECT)
class LocateToolHoldingService(private val project: Project) {
    private val lock = Any()

    @GuardedBy("lock")
    private var locateToolWindow: LocateToolWindow? = null
    @GuardedBy("lock")
    private var _locatorType: LocatorType = synchronized(lock) {
        val locatorTypeValue = PropertiesComponent.getInstance(project)
            .getValue(locatorTypePersistanceKey)
        LocatorType::class.sealedSubclasses
            .firstOrNull { it.simpleName == locatorTypeValue }
            ?.objectInstance
            ?: DroidUiSelectorLocatorType
    }

    var locatorType: LocatorType
        get() = synchronized(lock) {
            _locatorType
        }
        set(value) = synchronized(lock) {
            _locatorType = value

            PropertiesComponent.getInstance(project)
                .setValue(locatorTypePersistanceKey, value.javaClass.simpleName, null)

            AppUIUtil.invokeLaterIfProjectAlive(project) {
                project.messageBus.syncPublisher(LocatorTypeChangedListener.topic)
                    .onLocatorTypeChanged(value)
            }
        }

    companion object {
        private val locatorTypePersistanceKey = "${LocateToolHoldingService::class.java.name}.locatorType"
    }

    fun registerToolWindow(toolWindow: LocateToolWindow) = synchronized(lock) {
        locateToolWindow = toolWindow
    }

    fun getCurrentLocator(): String? = synchronized(lock) {
        return locateToolWindow?.getCurrentLocator()
    }
}
