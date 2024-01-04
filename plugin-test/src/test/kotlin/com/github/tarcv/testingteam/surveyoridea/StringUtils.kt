package com.github.tarcv.testingteam.surveyoridea

fun String.trimAllIndent() = trim().replace(Regex("\\s+"), " ")
