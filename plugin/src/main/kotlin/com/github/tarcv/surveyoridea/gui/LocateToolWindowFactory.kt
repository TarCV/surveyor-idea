package com.github.tarcv.surveyoridea.gui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class LocateToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val locateToolWindow = LocateToolWindow(project, toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(locateToolWindow.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
