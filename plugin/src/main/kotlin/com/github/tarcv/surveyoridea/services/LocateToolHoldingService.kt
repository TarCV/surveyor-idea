package com.github.tarcv.surveyoridea.services

import com.github.tarcv.surveyoridea.gui.LocateToolWindow
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import javax.annotation.concurrent.GuardedBy

@Service
class LocateToolHoldingService(project: Project) {
    private val lock = Any()

    @GuardedBy("lock")
    private var locateToolWindow: LocateToolWindow? = null

    fun registerToolWindow(toolWindow: LocateToolWindow) = synchronized(lock) {
        locateToolWindow = toolWindow
    }

    fun getCurrentLocator(): String? = synchronized(lock) {
        return locateToolWindow?.getCurrentLocator()
    }
}
