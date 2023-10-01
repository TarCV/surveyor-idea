package com.github.tarcv.testingteam.surveyoridea.gui

import com.automation.remarks.junit5.Video
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.IdeaFrame
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.trimAllIndent
import com.github.tarcv.testingteam.surveyoridea.waitingAssertion
import com.intellij.remoterobot.utils.keyboard
import org.apache.commons.lang.StringEscapeUtils
import org.junit.jupiter.api.Test
import java.awt.event.KeyEvent
import java.lang.Thread.sleep

class LocateActionUiTests : BaseTestProjectTests() {
    @Test
    @Video
    fun testLocatingFromKeyboard() = verifyLocatingFrom {
        locateElementToolWindow {
            editor.keyboard {
                if (remoteRobot.isMac()) {
                    pressing(KeyEvent.VK_META) {
                        enter()
                    }
                } else {
                    pressing(KeyEvent.VK_CONTROL) {
                        enter()
                    }
                }
            }
        }
    }

    @Test
    @Video
    fun testLocatingFromMenu() = verifyLocatingFrom {
        locateElementToolWindow {
            menuBar.select(
                "Edit",
                "Find",
                "Locate Element"
            )
        }
    }

    @Test
    @Video
    fun testLocatingFromToolButton() = verifyLocatingFrom {
        locateElementToolWindow {
            locateButton.click()
        }
    }

    private fun verifyLocatingFrom(triggerActionWithBlock: IdeaFrame.() -> Unit) = with(remoteRobot) {
        idea {
            openFileInTestProject(droidAutomatorSnapshotFile, "editorWithSnapshot")

            menuBar.select(
                "View",
                "Tool Windows",
                "Locate Element"
            )

            // Opening 'Locate Element' tool window sometimes causes reindexing
            sleep(2_000)
            commonSteps.waitForSmartMode(1)

            locateElementToolWindow {
                // Escaping is required due to simple concatenation in the text#set implementation
                editor.text =
                    StringEscapeUtils.escapeJavaScript("""new UiSelector().resourceIdMatches(".+/celsiusText")""")
            }
            triggerActionWithBlock()

            waitingAssertion(
                "Correct node should be selected.",
                """
                    <node index="4" text="-0.00" resource-id="com.github.tarcv.converter:id/celsiusText"
                        class="android.widget.EditText" package="com.github.tarcv.converter" content-desc=""
                        checkable="false" checked="false" clickable="true" enabled="true" focusable="true"
                        focused="true" scrollable="false" long-clickable="true" password="false"
                        selected="false" bounds="[250,933][830,1057]"/>
                    """.trimAllIndent()
            ) {
                getSelectedXmlNodeOuterXml("editorWithSnapshot").trimAllIndent()
            }
        }
    }
}
