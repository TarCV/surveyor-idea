package com.github.tarcv.testingteam.surveyoridea.gui.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.fixtures.JTextAreaFixture
import com.intellij.remoterobot.stepsProcessing.step
import java.time.Duration

fun RemoteRobot.noticeFrame(function: NoticeFrame.() -> Unit) {
    find<NoticeFrame>(timeout = Duration.ofSeconds(10)).apply(function)
}

@FixtureName("Notice frame")
@DefaultXpath("IdeFrameImpl type", "//div[@class='IdeFrameImpl']")
class NoticeFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {

    val overallIntro
        get() = step("With the overall intro text area") {
            return@step findAll<JTextAreaFixture>(JTextAreaFixture.byType())[0]
        }

    val noticeIntro
        get() = step("With the notice intro text area") {
            return@step findAll<JTextAreaFixture>(JTextAreaFixture.byType())[1]
        }

    val noticeText
        get() = step("With the notice text area") {
            return@step findAll<JTextAreaFixture>(JTextAreaFixture.byType())[2]
        }
}