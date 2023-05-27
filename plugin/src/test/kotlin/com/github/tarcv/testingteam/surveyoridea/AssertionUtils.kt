package com.github.tarcv.testingteam.surveyoridea

import com.intellij.remoterobot.utils.waitFor

fun <T> waitingAssertion(errorMessage: String, expectedValue: List<String>, valueSupplier: () -> T) {
    var lastValue: T? = null
    waitFor(errorMessageSupplier = { "$errorMessage Actual value was $lastValue" }) {
        lastValue = valueSupplier()
        lastValue == expectedValue
    }
}