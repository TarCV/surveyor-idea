package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.IdeaFrame
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.trimAllIndent
import com.github.tarcv.testingteam.surveyoridea.waitingAssertEquals
import com.intellij.remoterobot.utils.keyboard
import org.apache.commons.text.StringEscapeUtils
import org.junit.jupiter.api.Test
import java.awt.Point
import java.awt.event.KeyEvent
import java.lang.Thread.sleep

private const val singleLineLocator = """new UiSelector().resourceIdMatches(".+/celsiusText")"""
private const val multiLineLocator = """new UiSelector()${'\n'}.resourceIdMatches(${'\n'}".+/celsiusText")"""

class LocateActionUiTests : BaseTestProjectTests() {
    @Test
    fun testLocatingFromKeyboard() = verifyLocatingFrom(singleLineLocator) {
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
    fun testLocatingFromMenu() = verifyLocatingFrom(singleLineLocator) {
        locateElementToolWindow {
            selectInMenuBar(
                "Edit",
                "Find",
                "Locate Element"
            )
        }
    }

    @Test
    fun testLocatingFromToolButton() = assertLocatingFromToolButton(singleLineLocator)

    @Test
    fun testLocatingMultilineFromToolButton() = assertLocatingFromToolButton(multiLineLocator)

    private fun assertLocatingFromToolButton(locator: String) {
        verifyLocatingFrom(locator) {
            locateElementToolWindow {
                locateButton.click()
            }
        }
    }

    private fun verifyLocatingFrom(locator: String, triggerActionWithBlock: IdeaFrame.() -> Unit) = with(remoteRobot) {
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
                            StringEscapeUtils.escapeEcmaScript(locator)
                                .replace("\\\"", "\"")
                        )

                        repeat(5) { // make sure there is no extra automagically added braces
                            key(KeyEvent.VK_DELETE)
                        }
                    }
                }
            }
            triggerActionWithBlock()

            waitingAssertEquals(
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
