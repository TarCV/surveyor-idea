package com.github.tarcv.testingteam.surveyor.uiautomator

import com.github.tarcv.testingteam.surveyor.Evaluator
import com.github.tarcv.testingteam.surveyor.Node
import org.junit.jupiter.api.Test

class SmokeTests {
    @Test
    fun evaluatorCanLoadSelectorClasses() {
        Evaluator().evaluate(
            Node(null, emptyMap(), emptyList(), true),
            """import androidx.test.uiautomator.UiSelector;
                |import androidx.test.uiautomator.By;
                | new UiSelector();
                | By.clazz("clazz")"""
                .trimMargin()
        )
    }
}