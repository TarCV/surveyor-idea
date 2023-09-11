package com.github.tarcv.testingteam.surveyoridea.gui

import com.automation.remarks.junit5.Video
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.trimAllIndent
import com.github.tarcv.testingteam.surveyoridea.waitingAssertion
import com.intellij.remoterobot.fixtures.ActionButtonFixture.PopState.PUSHED
import com.intellij.remoterobot.fixtures.ActionButtonFixture.PopState.SELECTED
import com.intellij.remoterobot.utils.waitForIgnoringError
import org.junit.jupiter.api.Test

class StructureTest : BaseTestProjectTests() {
    @Test
    @Video
    fun testDroidSnapshotNavigationFromStructure() = with(remoteRobot) {
        idea {
            openFileInTestProject(droidAutomatorSnapshotFile, "editorWithSnapshot")

            menuBar.select(
                "View",
                "Tool Windows",
                "Structure"
            )

            // this is both an Act and an Assertion that this path exists
            waitForIgnoringError {
                structureTree.expandAll()
                structureTree.doubleClickPath(
                    "dump.uix",
                    "FrameLayout",
                    "LinearLayout",
                    "FrameLayout",
                    "ViewGroup decor_content_parent",
                    "FrameLayout content",
                    "ViewGroup rootLayout",
                    "EditText fahrenheitText",
                    fullMatch = true
                )
                true
            }

            waitingAssertion(
                "Correct node should be selected",
                """
                    <node index="6" text="32.00" resource-id="com.github.tarcv.converter:id/fahrenheitText"
                                  class="android.widget.EditText" package="com.github.tarcv.converter" content-desc=""
                                  checkable="false" checked="false" clickable="true" enabled="true" focusable="true"
                                  focused="false" scrollable="false" long-clickable="true" password="false"
                                  selected="false" bounds="[250,1140][830,1264]"/>
                    """.trimAllIndent()
            ) {
                getSelectedXmlNodeOuterXml("editorWithSnapshot").trimAllIndent()
            }
        }
    }

    @Test
    @Video
    fun testDroidSnapshotHighlightInStructure() = with(remoteRobot) {
        idea {
            openFileInTestProject(droidAutomatorSnapshotFile, "editorWithSnapshot")

            menuBar.select(
                "View",
                "Tool Windows",
                "Structure"
            )

            actionButtonByName("Always Select Opened Element").apply {
                if (popState().let { it != PUSHED && it != SELECTED }) {
                    click()
                }
            }

            textEditor().editor.clickOnOffset(6750)

            waitingAssertion(
                "Correct node should be selected.",
                listOf(
                    "dump.uix",
                    "FrameLayout",
                    "LinearLayout",
                    "FrameLayout",
                    "ViewGroup decor_content_parent",
                    "FrameLayout content",
                    "ViewGroup rootLayout",
                    "SeekBar seekBar",
                )
            ) { structureTree.collectSelectedPaths().singleOrNull() }
        }
    }
}