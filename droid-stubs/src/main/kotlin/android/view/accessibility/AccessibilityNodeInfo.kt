/*
 *  Copyright (C) 2023 TarCV
 *
 *  This package (i.e. directory and its contents) is part of UI Surveyor.
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
package android.view.accessibility

import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyor.Properties.ACCESSIBILITY_DESCRIPTION
import com.github.tarcv.testingteam.surveyor.Properties.CLASS_NAME
import com.github.tarcv.testingteam.surveyor.Properties.IS_CHECKABLE
import com.github.tarcv.testingteam.surveyor.Properties.IS_CHECKED
import com.github.tarcv.testingteam.surveyor.Properties.IS_CLICKABLE
import com.github.tarcv.testingteam.surveyor.Properties.IS_ENABLED
import com.github.tarcv.testingteam.surveyor.Properties.IS_FOCUSABLE
import com.github.tarcv.testingteam.surveyor.Properties.IS_FOCUSED
import com.github.tarcv.testingteam.surveyor.Properties.IS_LONG_CLICKABLE
import com.github.tarcv.testingteam.surveyor.Properties.IS_SCROLLABLE
import com.github.tarcv.testingteam.surveyor.Properties.IS_SELECTED
import com.github.tarcv.testingteam.surveyor.Properties.PACKAGE_NAME
import com.github.tarcv.testingteam.surveyor.Properties.RESOURCE_ID
import com.github.tarcv.testingteam.surveyor.Properties.TEXT

class AccessibilityNodeInfo(val node: Node) {
    companion object {
        @JvmStatic
        fun obtain(node: AccessibilityNodeInfo) = node
    }

    fun refresh(): Boolean = true

    val isVisibleToUser: Boolean
        get() = node.isVisible

    val parent: AccessibilityNodeInfo?
        get() = node.parent?.let {
            AccessibilityNodeInfo(it)
        }

    val className: CharSequence
        get() {
            return node.getProperty(CLASS_NAME)
        }

    val isClickable: Boolean
        get() {
            return node.getProperty(IS_CLICKABLE)
        }

    val contentDescription: CharSequence
        get() {
            return node.getProperty(ACCESSIBILITY_DESCRIPTION)
        }

    val isEnabled: Boolean
        get() {
            return node.getProperty(IS_ENABLED)
        }

    val isFocused: Boolean
        get() {
            return node.getProperty(IS_FOCUSED)
        }

    val isFocusable: Boolean
        get() {
            return node.getProperty(IS_FOCUSABLE)
        }

    val isLongClickable: Boolean
        get() {
            return node.getProperty(IS_LONG_CLICKABLE)
        }

    val isScrollable: Boolean
        get() {
            return node.getProperty(IS_SCROLLABLE)
        }

    val isSelected: Boolean
        get() {
            return node.getProperty(IS_SELECTED)
        }

    val packageName: CharSequence
        get() {
            return node.getProperty(PACKAGE_NAME)
        }


    val viewIdResourceName: String
        get() {
            return node.getProperty(RESOURCE_ID)
        }


    val text: CharSequence
        get() {
            return node.getProperty(TEXT)
        }


    val isChecked: Boolean
        get() {
            return node.getProperty(IS_CHECKED)
        }


    val isCheckable: Boolean
        get() {
            return node.getProperty(IS_CHECKABLE)
        }


    fun getChild(index: Int): AccessibilityNodeInfo = AccessibilityNodeInfo(node.children[index])

    val childCount: Int
        get() = node.children.size
}
