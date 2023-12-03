package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.IdeaFrame
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.trimAllIndent
import com.github.tarcv.testingteam.surveyoridea.waitingAssertion
import com.intellij.remoterobot.utils.keyboard
import org.apache.commons.text.StringEscapeUtils
import org.junit.jupiter.api.Test
import java.awt.Point
import java.awt.event.KeyEvent
import java.lang.Thread.sleep

class LocateActionUiTests : BaseTestProjectTests() {
    @Test
    fun testLocatingFromKeyboard() = verifyLocatingFrom {
        locateElementToolWindow {
            editor.apply {
                keyboard {
                    val specialKey = when {
                        remoteRobot.isMac() -> KeyEvent.VK_META // Command key
                        else -> KeyEvent.VK_CONTROL
                    }
                    pressing(specialKey) {
                        enter()
                    }
                }
            }
        }
    }

    @Test
    fun testLocatingFromMenu() = verifyLocatingFrom {
        locateElementToolWindow {
            selectInMenuBar(
                "Edit",
                "Find",
                "Locate Element"
            )
        }
    }

    @Test
    fun testLocatingFromToolButton() = verifyLocatingFrom {
        locateElementToolWindow {
            locateButton.click()
        }
    }

    private fun verifyLocatingFrom(triggerActionWithBlock: IdeaFrame.() -> Unit) = with(remoteRobot) {
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
                editor.apply {
                    sleep(5_000)
                    click(Point(5, 5))
                    sleep(5_000)

                    keyboard {
                        selectAll()

                        // Escaping is required due to how enterText is implemented
                        enterText(
                            StringEscapeUtils.escapeEcmaScript("""new UiSelector().resourceIdMatches(".+/celsiusText")""")
                                .replace("\\\"", "\"")
                        )
                    }
                }
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
