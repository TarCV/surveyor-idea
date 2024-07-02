package com.github.tarcv.testingteam.surveyoridea.gui.fixtures

import com.github.tarcv.testingteam.surveyoridea.waitingAssertEquals
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ActionButtonFixture
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.EditorFixture
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.fixtures.HeavyWeightWindowFixture
import com.intellij.remoterobot.fixtures.JButtonFixture
import com.intellij.remoterobot.utils.Locators.XpathProperty
import com.intellij.remoterobot.utils.Locators.byPropertiesContains
import java.time.Duration

fun ContainerFixture.locateElementToolWindow(func: LocateElementToolWindowFixture.() -> Unit = {}) =
    find<LocateElementToolWindowFixture>(timeout = Duration.ofSeconds(10)).apply(func)

@FixtureName("Locate Element Tool Window")
@DefaultXpath("accessible name", "//div[@accessiblename='Locate Element Tool Window']")
class LocateElementToolWindowFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {

    val locatorTypeDropdown: JButtonFixture
        get() = find(byPropertiesContains(XpathProperty.JAVA_CLASS to "ComboBox"))

    val locateButton: ActionButtonFixture
        get() = find(ActionButtonFixture.byTooltipText("Locate Element"))

    val noticeButton: ActionButtonFixture
        get() = find(ActionButtonFixture.byTooltipText("Show Licenses and Notices"))

    val editor: EditorFixture
        get() = find(EditorFixture.locator, Duration.ofSeconds(10))

    fun selectLocatorType(type: String) {
        locatorTypeDropdown.click()
        remoteRobot.find(
            HeavyWeightWindowFixture::class.java,
            HeavyWeightWindowFixture.byXpath,
            Duration.ofSeconds(5)
        ).apply {
            try {
                itemsList.clickItem(type.take(17), fullMatch = false)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "'$type' not found. The following items are present instead: ${itemsList.collectItems()}",
                    e
                )
            }
        }
        waitingAssertEquals("New locator type should be '$type'", type) {
            locatorTypeDropdown.text
        }
    }
}