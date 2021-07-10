package android.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.github.tarcv.testingteam.surveyor.Node

@Suppress("UNUSED_PARAMETER", "unused")
class UiAutomation(rootNode: Node) {
    private val rootNode = AccessibilityNodeInfo(rootNode)

    var onAccessibilityEventListener: OnAccessibilityEventListener? = null
    var serviceInfo: AccessibilityServiceInfo = AccessibilityServiceInfo()

    interface AccessibilityEventFilter
    interface OnAccessibilityEventListener

    fun waitForIdle(a: Long, b: Long) {
        // no op
    }

    fun getRootInActiveWindow(): AccessibilityNodeInfo {
        return rootNode
    }

    fun getWindows(): List<AccessibilityWindowInfo> {
        // TODO: support multiple roots
        return listOf(AccessibilityWindowInfo(getRootInActiveWindow()))
    }
}
