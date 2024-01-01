package com.github.tarcv.testingteam.surveyoridea

import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

fun <T> waitingAssertEquals(errorMessage: String, expectedValue: T, valueSupplier: () -> T) {
    return waitingAssertion(
        errorMessage,
        valueSupplier
    ) { it == expectedValue }
}

fun <T> waitingAssertion(errorMessage: String, valueSupplier: () -> T, assertion: (T) -> Boolean) {
    var lastValue: T? = null
    waitFor(Duration.ofSeconds(20), errorMessageSupplier = { "$errorMessage. Actual value was: $lastValue" }) {
        val value = valueSupplier()
        lastValue = value
        assertion(value)
    }
}