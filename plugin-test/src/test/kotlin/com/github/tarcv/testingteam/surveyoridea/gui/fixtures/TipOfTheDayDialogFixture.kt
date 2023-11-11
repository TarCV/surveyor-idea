package com.github.tarcv.testingteam.surveyoridea.gui.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.utils.WaitForConditionTimeoutException

inline fun CommonContainerFixture.ifTipOfTheDayDialogPresent(crossinline block: TipOfTheDayDialogFixture.() -> Unit) {
    val fixture = try {
        tipOfTheDayDialog {
            // no op just to call invoke in the dialog
        }
    } catch (e: WaitForConditionTimeoutException) {
        return
    }
    block.invoke(fixture)
}

inline fun ContainerFixture.tipOfTheDayDialog(func: TipOfTheDayDialogFixture.() -> Unit = {}) =
    find<TipOfTheDayDialogFixture>(TipOfTheDayDialogFixture.byXPath()).apply(func)

class TipOfTheDayDialogFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : CommonContainerFixture(remoteRobot, remoteComponent) {
    companion object {
        fun byXPath(): Locator {
            return byXpath(
                "MyDialog with title Tip of the Day",
                "//div[@title='Tip of the Day' and @class='MyDialog']"
            )
        }
    }

    fun close() = button("Close").click()
}