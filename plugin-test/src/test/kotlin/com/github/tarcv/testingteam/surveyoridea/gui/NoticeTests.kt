package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.noticeFrame
import com.github.tarcv.testingteam.surveyoridea.waitingAssertion
import com.intellij.remoterobot.utils.keyboard
import org.junit.jupiter.api.Test

class NoticeTests : BaseTestProjectTests() {
    @Test
    fun testNotices() = with(remoteRobot) {
        idea {
            openFileAndToolWindow(relativeToProject(droidAutomatorSnapshotFile), editorWithSnapshot)
            locateElementToolWindow {
                noticeButton.click()
            }
        }
        try {
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
        } finally {
            keyboard { escape() }
        }
    }
}
