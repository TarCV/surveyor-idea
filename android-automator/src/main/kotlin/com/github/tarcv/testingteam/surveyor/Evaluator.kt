/*
 *  UiAutomator plugin for TestingTeam-Surveyor
 *  This program, except the code under src/main/kotlin/android directory, is
 *
 *  Copyright (C) 2021 TarCV
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.tarcv.testingteam.surveyor

import android.app.Instrumentation
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiSelector

class Evaluator {
    fun evaluateUiSelector(rootNode: Node, selector: UiSelector): Node? {
        return uiDeviceFromRootNode(rootNode)
            .findObject(selector)
            .let { extractNode(it) }
    }

    fun evaluateBySelector(rootNode: Node, selector: BySelector): Node? {
        return uiDeviceFromRootNode(rootNode)
            .findObject(selector)
            ?.let { extractNode(it) }
    }

    private fun uiDeviceFromRootNode(rootNode: Node): UiDevice {
        val instrumentation = Instrumentation(rootNode)
        return UiDevice.getInstance(instrumentation)
    }

    private fun extractNode(obj: UiObject): Node? {
        return UiObject::class.java
            .getDeclaredMethod("findAccessibilityNodeInfo", Long::class.java)
            .also {
                it.isAccessible = true
            }
            .invoke(obj, 1000)
            ?.let { it as AccessibilityNodeInfo }
            ?.node
    }

    private fun extractNode(obj: UiObject2): Node {
        return UiObject2::class.java
            .getDeclaredMethod("getAccessibilityNodeInfo")
            .also {
                it.isAccessible = true
            }
            .invoke(obj, 1000)
            .let { it as AccessibilityNodeInfo }
            .node
    }
}