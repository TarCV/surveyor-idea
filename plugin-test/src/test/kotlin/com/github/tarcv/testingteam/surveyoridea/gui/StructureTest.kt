package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.trimAllIndent
import com.github.tarcv.testingteam.surveyoridea.waitingAssertEquals
import com.github.tarcv.testingteam.surveyoridea.waitingAssertion
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.client.IdeaSideException
import com.intellij.remoterobot.fixtures.ActionButtonFixture.PopState.PUSHED
import com.intellij.remoterobot.fixtures.ActionButtonFixture.PopState.SELECTED
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.HeavyWeightWindowFixture
import com.intellij.remoterobot.fixtures.JListFixture
import com.intellij.remoterobot.fixtures.JListFixture.Companion.byItem
import com.intellij.remoterobot.fixtures.JTreeFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.steps.CommonSteps
import com.intellij.remoterobot.utils.attempt
import com.intellij.remoterobot.utils.waitForIgnoringError
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration

class StructureTest : BaseTestProjectTests() {
    @MethodSource("getDroidAutomatorSnapshotVariant")
    @ParameterizedTest
    // TODO: Test navigation for the bar for both snapshot types
    fun testDroidSnapshotNavigationFromStructure(snapshot: Snapshot) =
        verifySnapshotNavigationFromStructure(
            snapshot.relativePath,
            arrayOf(
                snapshot.relativePath.substringAfterLast('/'),
                "FrameLayout",
                "LinearLayout",
                "FrameLayout",
                "ViewGroup decor_content_parent",
                "FrameLayout content",
                "ViewGroup rootLayout",
                "EditText fahrenheitText",
            ),
            """
                <node index="6" text="32.00" resource-id="com.github.tarcv.converter:id/fahrenheitText"
                              class="android.widget.EditText" package="com.github.tarcv.converter" content-desc=""
                              checkable="false" checked="false" clickable="true" enabled="true" focusable="true"
                              focused="false" scrollable="false" long-clickable="true" password="false"
                              selected="false"
                """,
            """bounds="[250,1140][830,1264]""""
        )

    @MethodSource("getDroidAutomatorSnapshotVariant")
    @Tag(REQUIRES_SCREENSHOT_ASSUMPTIONS_TAG)
    @ParameterizedTest
    fun screenshotDroidSnapshotNavigationFromStructure(snapshot: Snapshot) = with(remoteRobot) {
        verifySnapshotNavigationFromStructure(
            snapshot.relativePath,
            arrayOf(
                snapshot.relativePath.substringAfterLast('/'),
                "FrameLayout",
                "LinearLayout",
                "FrameLayout",
                "ViewGroup decor_content_parent",
                "FrameLayout content",
                "ViewGroup rootLayout",
                "EditText celsiusText",
            ),
            """
                    <node index="4" text="-0.00" resource-id="com.github.tarcv.converter:id/celsiusText"
                                  class="android.widget.EditText" package="com.github.tarcv.converter" content-desc=""
                                  checkable="false" checked="false" clickable="true" enabled="true" focusable="true"
                                  focused="true" scrollable="false" long-clickable="true" password="false"
                                  selected="false"
                    """,
            """bounds="[250,933][830,1057]""""
        )
        idea {
            attempt(tries = 2) {
                find<ComponentFixture>(
                    byXpath(
                        "//div[contains(@class,'NavBar') and @accessiblename='ViewGroup decor_content_parent']"
                    ),
                    timeout = Duration.ofSeconds(10)
                )
                    .click()
            }
            waitForIgnoringError {
                val expectedSibling = "FrameLayout action_bar_container"
                find<HeavyWeightWindowFixture>()
                    .find<JListFixture>(byItem(expectedSibling))
                    .collectItems()
                    .contains(expectedSibling)
            }
        }
    }

    @Test
    fun testIDeviceSnapshotNavigationFromStructure() = verifySnapshotNavigationFromStructure(
        iPredicateSnapshotFile,
        arrayOf(
            iPredicateSnapshotFile.substringAfterLast('/'),
            "Application IntegrationApp",
            "Window",
            "Other",
            "Other",
            "Other",
            "Other",
            "Other",
            "Other MainView",
            "Button Attributes",
        ),
        """
            <XCUIElementTypeButton type="XCUIElementTypeButton" name="Attributes" label="Attributes" enabled="true"
                              visible="true" x="173" y="197" width="68" height="30" index="2">
            """,
        ""
    )

    private fun verifySnapshotNavigationFromStructure(
        snapshotFile: String,
        path: Array<out String>,
        expectedNodeStartsWith: String,
        expectedNodeContains: String
    ) =
        with(remoteRobot) {
            idea {
                openFileInTestProject(relativeToProject(snapshotFile), editorWithSnapshot)
                openStructureToolwindow()

                // this is both an Act and an Assertion that this path exists
                attempt(tries = 2) {
                    structureTree.expandAllWorkaround()
                    structureTree.doubleClickPath( // this method selects wrong item on the first try
                        *path,
                        fullMatch = true
                    )

                    waitingAssertion(
                        "Correct node should be selected",
                        { getSelectedXmlNodeOuterXml(editorWithSnapshot).trimAllIndent() }
                    ) {
                        it.startsWith(expectedNodeStartsWith.trimAllIndent()) &&
                                it.contains(expectedNodeContains.trimAllIndent())
                    }
                }
            }
        }

    private fun JTreeFixture.expandAllWorkaround() {
        try {
            expandAll()
        } catch (e: IdeaSideException) {
            if (e.message?.contains("timeout", ignoreCase = true) == true) {
                // expandAll is broken in Robot 0.11.23
                e.printStackTrace()
            } else {
                throw e
            }
        }
    }

    private fun RemoteRobot.openStructureToolwindow() {
        CommonSteps(this)
            .invokeAction("ActivateStructureToolWindow")
    }

    @MethodSource("getDroidAutomatorSnapshotVariant")
    @ParameterizedTest
    fun testDroidSnapshotHighlightInStructure(snapshot: Snapshot) = verifySnapshotHighlighInStructure(
        snapshot.relativePath,
        snapshot.editorOffsetForStructureCheck,
        listOf(
            snapshot.relativePath.substringAfterLast('/'),
            "FrameLayout",
            "LinearLayout",
            "FrameLayout",
            "ViewGroup decor_content_parent",
            "FrameLayout content",
            "ViewGroup rootLayout",
            "SeekBar seekBar",
        )
    )

    @Test
    fun testIDeviceSnapshotHighlightInStructure() = verifySnapshotHighlighInStructure(
        iPredicateSnapshotFile,
        4400,
        listOf(
            iPredicateSnapshotFile.substringAfterLast('/'),
            "Application IntegrationApp",
            "Window",
            "Other",
            "Other",
            "Other",
            "Other",
            "Other",
            "Other MainView",
            "StaticText Portrait",
        )
    )

    private fun verifySnapshotHighlighInStructure(snapshotFile: String, editorOffset: Int, expectedPath: List<String>) {
        with(remoteRobot) {
            idea {
                openFileInTestProject(relativeToProject(snapshotFile), editorWithSnapshot)
                openStructureToolwindow()

                actionButtonByName("Always Select Opened Element").apply {
                    if (popState().let { it != PUSHED && it != SELECTED }) {
                        click()
                    }
                }

                textEditor().editor.clickOnOffset(editorOffset)

                waitingAssertEquals(
                    "Correct node should be selected.",
                    expectedPath
                ) { structureTree.collectSelectedPaths().singleOrNull() }
            }
        }
    }

    data class Snapshot(
        val relativePath: String,
        val editorOffsetForStructureCheck: Int
    )

    private companion object {
        @JvmStatic
        val droidAutomatorSnapshotVariant: List<Snapshot> = listOf(
            Snapshot(droidAutomatorSnapshotFile, editorOffsetForStructureCheck = 6750),
            Snapshot(droidAutomator23SnapshotFile, editorOffsetForStructureCheck = 7971)
        )
    }
}