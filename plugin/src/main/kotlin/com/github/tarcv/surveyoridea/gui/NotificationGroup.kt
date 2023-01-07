package com.github.tarcv.surveyoridea.gui

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project


private object NotificationGroup
private val notificationGroupId = NotificationGroup::class.java.canonicalName

fun Project.notify(
    content: String,
    notificationType: NotificationType
) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup(notificationGroupId)
        .createNotification(content, notificationType)
        .notify(this)
}