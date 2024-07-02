package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.ScreenshotTest
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.IdeaFrame
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.LocateElementToolWindowFixture
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.trimAllIndent
import com.github.tarcv.testingteam.surveyoridea.waitingAssertEquals
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.HeavyWeightWindowFixture
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.Locators.XpathProperty.TEXT
import com.intellij.remoterobot.utils.keyboard
import org.apache.commons.text.StringEscapeUtils
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.awt.Point
import java.awt.event.KeyEvent
import java.lang.Thread.sleep
import javax.swing.JMenuItem

const val droidAutomatorSelectorType = "BySelector or UISelector"
const val ipredicateSelectorType = "Appium/WDA Predicate"
const val iClasschainSelectorType = "Appium/WDA Class Chain"

class LocateActionUiTests : BaseTestProjectTests() {
    companion object {
        @JvmStatic
        fun locationExamples(): List<LocationSetupData> {
            return buildList {
                addDroidAutomatorExamples()
                addIPredicateExamples()
                this.addIClasschainExamples()
            }
        }

        private fun MutableList<LocationSetupData>.addDroidAutomatorExamples() {
            val expectedTag = """
                                    <node index="4" text="-0.00" resource-id="com.github.tarcv.converter:id/celsiusText"
                                        class="android.widget.EditText" package="com.github.tarcv.converter" content-desc=""
                                        checkable="false" checked="false" clickable="true" enabled="true" focusable="true"
                                        focused="true" scrollable="false" long-clickable="true" password="false"
                                        selected="false" bounds="[250,933][830,1057]"/>
                                    """.trimAllIndent()
            val singleLineSelector = """new UiSelector().resourceIdMatches(".+/celsiusText")"""
            add(
                LocationSetupData(
                    droidAutomatorSnapshotFile,
                    null,
                    singleLineSelector,
                    expectedTag
                )
            )
            add(
                LocationSetupData(
                    droidAutomatorSnapshotFile,
                    null,
                    """new UiSelector()${'\n'}.resourceIdMatches(${'\n'}".+/celsiusText")""",
                    expectedTag
                )
            )
            add(
                LocationSetupData(
                    droidAutomatorSnapshotFile,
                    {
                        selectLocatorType(ipredicateSelectorType)
                        selectLocatorType(droidAutomatorSelectorType)
                    },
                    singleLineSelector,
                    expectedTag
                )
            )
        }

        private fun MutableList<LocationSetupData>.addIPredicateExamples() {
            val expectedTag =
                """<XCUIElementTypeButton type="XCUIElementTypeButton" name="Scrolling" label="Scrolling" enabled="true" visible="true" x="177" y="235" width="60" height="30" index="3">"""
            add(
                LocationSetupData(
                    iPredicateSnapshotFile,
                    ipredicateSelectorType,
                    """type = 'XCUIElementTypeButton' AND${'\n'}name BEGINSWITH 'Scroll'""",
                    expectedTag
                )
            )
            add(
                LocationSetupData(
                    iPredicateSnapshotFile,
                    ipredicateSelectorType,
                    """type = 'XCUIElementTypeButton' AND name BEGINSWITH 'Scroll'""",
                    expectedTag
                )
            )
        }

        private fun MutableList<LocationSetupData>.addIClasschainExamples() {
            val expectedTag =
                """<XCUIElementTypeButton type="XCUIElementTypeButton" name="Attributes" label="Attributes" enabled="true" visible="true" x="173" y="197" width="68" height="30" index="2">"""
            val singleLineSelector =
                """XCUIElementTypeWindow/**/XCUIElementTypeButton[`name BEGINSWITH 'Attr'`]"""
            add(
                LocationSetupData(
                    iPredicateSnapshotFile,
                    iClasschainSelectorType,
                    singleLineSelector,
                    expectedTag
                )
            )
            add(
                LocationSetupData(
                    iPredicateSnapshotFile,
                    iClasschainSelectorType,
                    """XCUIElementTypeWindow/**/XCUIElementTypeButton[`name${'\n'}BEGINSWITH 'Attr'`]""",
                    expectedTag
                )
            )
            add(
                LocationSetupData(
                    iPredicateSnapshotFile,
                    {
                        selectLocatorType(ipredicateSelectorType)
                        selectLocatorType(droidAutomatorSelectorType)
                        selectLocatorType(iClasschainSelectorType)
                    },
                    singleLineSelector,
                    expectedTag
                )
            )
        }
    }

    data class LocationSetupData(
        var snapshotFile: String,
        var toolwindowSetup: LocateElementToolWindowFixture.() -> Unit,
        var locator: String,
        var expectedTag: String
    ) {
        constructor(
            snapshotFile: String,
            type: String?,
            locator: String,
            expectedTag: String
        ) : this(
            snapshotFile,
            {
                if (type != null) {
                    selectLocatorType(type)
                }
            },
            locator,
            expectedTag
        )
    }

    @MethodSource("locationExamples")
    @ParameterizedTest
    annotation class TestAllLocatorTypes

    @TestAllLocatorTypes
    fun testLocatingFromKeyboard(locationSetupData: LocationSetupData) =
        verifyLocatingFrom(locationSetupData) {
            locateElementToolWindow {
                editor.apply {
                    keyboard {
                        val specialKey = when {
                            remoteRobot.isMac() -> KeyEvent.VK_META // Command key
                            else -> KeyEvent.VK_CONTROL
                        }
                        repeat(5) {
                            // Make sure if Special+Enter breaks a line it is visible
                            key(KeyEvent.VK_LEFT)
                        }
                        pressing(specialKey) {
                            enter()
                        }
                    }
                }
            }
        }

    @TestAllLocatorTypes
    fun testLocatingFromMenu(locationSetupData: LocationSetupData) =
        verifyLocatingFrom(locationSetupData) {
            locateElementToolWindow {
                selectInMenuBar(
                    "Edit",
                    "Find",
                    "Locate Element"
                )
            }
        }

    @ScreenshotTest
    fun screenshotLocatingFromToolButton() {
        testLocatingFromToolButton(
            LocationSetupData(
                droidAutomatorSnapshotFile,
                null,
                """new UiSelector().textContains("ICE")""",
                """
                        <node index="1" text="ICE MELTING (0°C)" resource-id="" class="android.widget.Button"
                            package="com.github.tarcv.converter" content-desc="" checkable="false" checked="false"
                            clickable="true" enabled="true" focusable="true" focused="false" scrollable="false"
                            long-clickable="false" password="false" selected="false" bounds="[618,501][750,897]"/>
                """.trimAllIndent()
            )
        )
    }

    @TestAllLocatorTypes
    fun testLocatingFromToolButton(locationSetupData: LocationSetupData) {
        verifyLocatingFrom(locationSetupData) {
            locateElementToolWindow {
                locateButton.click()
            }
        }
    }

    @ScreenshotTest
    fun screenshotLocatorTypes() = with(remoteRobot) {
        Assumptions.assumeTrue(isLinux())
        idea {
            openFileAndToolWindow(relativeToProject(droidAutomatorSnapshotFile), editorWithSnapshot)
            locateElementToolWindow {
                locatorTypeDropdown.click()
                sleep(5_000)
            }
        }
    }

    @ScreenshotTest
    fun screenshotOpeningToolWindowFromMainMenu() = with(remoteRobot) {
        Assumptions.assumeTrue(isLinux())
        idea {
            openFileInTestProject(relativeToProject(droidAutomatorSnapshotFile), editorWithSnapshot)
            selectInMenuBar(
                "View",
                "Tool Windows"
            )
            findAll<HeavyWeightWindowFixture>()
                .last()
                .findAll<ComponentFixture>(
                    Locators.byTypeAndProperties(
                        JMenuItem::class.java,
                        TEXT to "Locate Element"
                    )
                )
                .last()
                .moveMouse()
        }
    }

    @Test
    fun testOpeningToolWindowFromMainMenu() = with(remoteRobot) {
        idea {
            openFileInTestProject(relativeToProject(droidAutomatorSnapshotFile), editorWithSnapshot)
            selectInMenuBar(
                "View",
                "Tool Windows",
                "Locate Element"
            )
            locateElementToolWindow {
                editor
            }
        }
    }

    private fun verifyLocatingFrom(
        locationSetupData: LocationSetupData,
        triggerActionWithBlock: IdeaFrame.() -> Unit
    ) = with(remoteRobot) {
        idea {
            openFileAndToolWindow(relativeToProject(locationSetupData.snapshotFile), editorWithSnapshot)
            locateElementToolWindow {
                locationSetupData.toolwindowSetup(this)
                editor.apply {
                    sleep(5_000)
                    click(Point(5, 5))
                    sleep(5_000)

                    keyboard {
                        selectAll()

                        // Escaping is required due to how enterText is implemented
                        enterText(
                            StringEscapeUtils.escapeEcmaScript(locationSetupData.locator)
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
                locationSetupData.expectedTag
            ) {
                getSelectedXmlNodeOuterXml(editorWithSnapshot).trimAllIndent()
            }
        }
    }
}
