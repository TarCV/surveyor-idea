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
package com.github.tarcv.testingteam.surveyor.uiautomator

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiSelector
import com.github.tarcv.testingteam.surveyor.Evaluator
import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyor.Properties
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EvaluatorTests {
    val rootNode = run {
        val properties = mapOf<Properties<*>, Any?>(
            Properties.IS_CHECKABLE to false,
            Properties.IS_CHECKED to false,
            Properties.CLASS_NAME to "clazz",
            Properties.IS_CLICKABLE to false,
            Properties.ACCESSIBILITY_DESCRIPTION to "desc",
            Properties.IS_ENABLED to false,
            Properties.IS_FOCUSABLE to false,
            Properties.IS_FOCUSED to false,
            Properties.IS_LONG_CLICKABLE to false,
            Properties.PACKAGE_NAME to "pkg",
            Properties.IS_PASSWORD_FIELD to false,
            Properties.RESOURCE_ID to "id",
            Properties.IS_SCROLLABLE to false,
            Properties.IS_SELECTED to false,
            Properties.TEXT to "text"
        )
        Node(null, properties, emptyList(), true)
    }

    @Test
    fun evaluatorCanLoadSelectorClasses() {
        Evaluator().evaluate(
            rootNode,
            """//import ${UiSelector::class.java.canonicalName};
                |import ${By::class.java.canonicalName};
                |//new UiSelector();
                |By.clazz("clazz")"""
                .trimMargin()
        )
    }

    @Test
    fun evaluatorDeviceIsReset() {
        val evaluator = Evaluator()
        val firstDevice = evaluator.withUiDeviceFrom(rootNode) {
            this
        }
        val secondDevice = evaluator.withUiDeviceFrom(rootNode) {
            this
        }
        Assertions.assertNotSame(firstDevice, secondDevice)
    }
}