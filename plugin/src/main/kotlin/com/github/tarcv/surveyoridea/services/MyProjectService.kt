package com.github.tarcv.surveyoridea.services

import com.github.tarcv.surveyoridea.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
