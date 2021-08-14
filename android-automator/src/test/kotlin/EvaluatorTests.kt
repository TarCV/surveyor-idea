package com.github.tarcv.testingteam.surveyor.uiautomator

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
            """import androidx.test.uiautomator.UiSelector;
                |import androidx.test.uiautomator.By;
                | new UiSelector();
                | By.clazz("clazz")"""
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