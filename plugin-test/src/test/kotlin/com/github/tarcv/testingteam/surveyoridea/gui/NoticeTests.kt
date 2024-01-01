package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.noticeFrame
import com.github.tarcv.testingteam.surveyoridea.waitingAssertion
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class NoticeTests : BaseTestProjectTests() {
    @Test
    fun testLocatingFromToolButton() = with(remoteRobot) {
        idea {
            openFileInTestProject(droidAutomatorSnapshotFile, "editorWithSnapshot")

            selectInMenuBar(
                "View",
                "Tool Windows",
                "Locate Element"
            )

            // Opening 'Locate Element' tool window sometimes causes reindexing
            sleep(2_000)
            commonSteps.waitForSmartMode(1)

            locateElementToolWindow {
                noticeButton.click()
            }
        }
        noticeFrame {
            val overallIntroFragment = "depends on libraries which are covered by"

            waitingAssertion("Correct text is present in the overall intro area", { overallIntro.text }) {
                it.contains(overallIntroFragment)
            }

            jList {
                clickItem("UIAutomator library - Apache License")
            }
            waitingAssertion("Correct text is present in the overall intro area", { overallIntro.text }) {
                it.contains(overallIntroFragment)
            }
            waitingAssertion("Correct text is present in the notice intro area", { noticeIntro.text }) {
                it.contains("library which is covered by")
            }
            waitingAssertion("Correct text is present in the notice area", { noticeText.text }) {
                it.contains("Licensed under the Apache License")
            }
        }
    }
}
