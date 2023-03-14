package com.github.tarcv.testingteam.surveyoridea

import com.github.tarcv.testingteam.surveyoridea.gui.BaseTestProjectTests.Companion.REQUIRES_SCREENSHOT_ASSUMPTIONS_TAG
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(FUNCTION)
@Tag(REQUIRES_SCREENSHOT_ASSUMPTIONS_TAG)
@Test
annotation class ScreenshotTest