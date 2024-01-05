package com.github.tarcv.testingteam.surveyoridea.services

import com.github.tarcv.testingteam.surveyoridea.data.LocatorType
import com.intellij.util.messages.Topic

interface LocatorTypeChangedListener {
    companion object {
        val topic = Topic(
            "${LocatorTypeChangedListener::class.java.name}Topic",
            LocatorTypeChangedListener::class.java
        )
    }

    fun onLocatorTypeChanged(newType: LocatorType?)
}
