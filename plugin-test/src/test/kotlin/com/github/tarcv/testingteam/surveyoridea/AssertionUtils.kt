package com.github.tarcv.testingteam.surveyoridea

import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

fun <T> waitingAssertion(errorMessage: String, expectedValue: T, valueSupplier: () -> T) {
    var lastValue: T? = null
    waitFor(Duration.ofSeconds(20), errorMessageSupplier = { "$errorMessage Actual value was $lastValue" }) {
        lastValue = valueSupplier()
        lastValue == expectedValue
    }
}