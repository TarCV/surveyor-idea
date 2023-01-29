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
package com.github.tarcv.testingteam.surveyor

import android.app.Instrumentation
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiSelector
import bsh.Interpreter
import java.io.Closeable

class Evaluator {
    fun evaluate(rootNode: Node, locator: String): Node? {
        val limitingClassloader = LimitedClassloader(javaClass.classLoader)
        val interpreter = Interpreter().apply {
            setClassLoader(limitingClassloader)
            strictJava = true
        }
        return when (val it = interpreter.eval(locator)) {
            is UiSelector -> evaluateUiSelector(rootNode, it)
            is BySelector -> evaluateBySelector(rootNode, it)
            else -> {
                val typeName = it?.javaClass ?: "<null>"
                throw InvalidLocatorException("Expression returned unexpected value of type $typeName")
            }
        }
    }

    internal fun evaluateUiSelector(rootNode: Node, selector: UiSelector): Node? {
        return withUiDeviceFrom(rootNode) {
            extractNode(findObject(selector))
        }
    }

    internal fun evaluateBySelector(rootNode: Node, selector: BySelector): Node? {
        return withUiDeviceFrom(rootNode) {
            findObject(selector)
                ?.let { extractNode(it) }
        }
    }

    internal fun <T> withUiDeviceFrom(rootNode: Node, block: UiDevice.() -> T): T {
        val instanceResetter = Closeable {
            UiDevice::class.java
                .getDeclaredField("sInstance")
                .apply {
                    isAccessible = true
                }
                .set(null, null)
        }
        val instrumentation = Instrumentation(rootNode)
        return instanceResetter.use {
            val device = UiDevice.getInstance(instrumentation)
            block(device)
        }
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
            .invoke(obj)
            .let { it as AccessibilityNodeInfo }
            .node
    }
}