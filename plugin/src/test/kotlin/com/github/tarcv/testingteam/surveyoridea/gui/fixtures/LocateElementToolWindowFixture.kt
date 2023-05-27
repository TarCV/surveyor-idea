package com.github.tarcv.testingteam.surveyoridea.gui.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ActionButtonFixture
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.EditorFixture
import com.intellij.remoterobot.fixtures.FixtureName
import java.time.Duration

fun ContainerFixture.locateElementToolWindow(func: LocateElementToolWindowFixture.() -> Unit = {}) =
    find<LocateElementToolWindowFixture>(timeout = Duration.ofSeconds(10)).apply(func)

@FixtureName("Locate Element Tool Window")
@DefaultXpath("accessible name", "//div[@accessiblename='Locate Element Tool Window']")
class LocateElementToolWindowFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {

    val locateButton: ActionButtonFixture
        get() = find(ActionButtonFixture.byTooltipText("Locate Element"))

    val editor: EditorFixture
        get() = find(EditorFixture.locator, Duration.ofSeconds(10))
}