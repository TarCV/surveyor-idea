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
package android.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.util.SparseArray
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.github.tarcv.testingteam.surveyor.Node

@Suppress("UNUSED_PARAMETER", "unused")
class UiAutomation(rootNodes: List<Node>) {
    private val rootNodes = rootNodes.map{ AccessibilityNodeInfo(it) }

    var onAccessibilityEventListener: OnAccessibilityEventListener? = null
    var serviceInfo: AccessibilityServiceInfo = AccessibilityServiceInfo()

    interface AccessibilityEventFilter
    interface OnAccessibilityEventListener

    fun waitForIdle(a: Long, b: Long) {
        // no op
    }

    fun getRootInActiveWindow(): AccessibilityNodeInfo {
        return rootNodes.last()
    }

    fun getWindows(): List<AccessibilityWindowInfo> {
        return rootNodes.map { it.window }
    }

    fun getWindowsOnAllDisplays(): SparseArray<List<AccessibilityWindowInfo>> {
        val out = SparseArray<List<AccessibilityWindowInfo>>()
        rootNodes
            .groupBy { it.displayId }
            .forEach { (k, v) ->
                out.put(
                    k,
                    v.map { it.window }
                )
            }
        return out
    }
}
