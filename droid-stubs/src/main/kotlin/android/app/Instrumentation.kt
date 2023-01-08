package android.app

import android.content.Context
import com.github.tarcv.testingteam.surveyor.Node

@Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
class Instrumentation(rootNode: Node) {
    val uiAutomation: UiAutomation = UiAutomation(rootNode)
    val context: Context = Context()

    fun getUiAutomation(flags: Int): UiAutomation = uiAutomation
}