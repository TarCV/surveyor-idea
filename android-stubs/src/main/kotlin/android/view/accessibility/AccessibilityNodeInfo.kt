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
import com.github.tarcv.testingteam.surveyor.Properties.IS_LONGCLICKABLE
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
            return node.getProperty(IS_LONGCLICKABLE)
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


    val childCount: Int
        get() = node.children.size
}
