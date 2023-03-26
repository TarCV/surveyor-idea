package com.github.tarcv.testingteam.surveyoridea.filetypes

import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.ActualCodeElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomFileElement
import com.intellij.util.xml.DomManager
import java.util.IdentityHashMap

sealed interface FileType {
    companion object {
        fun <T : DomElement> tryReadAsXml(
            project: Project,
            xmlFile: PsiFile,
            rootClass: Class<T>
        ): DomFileElement<T>? {
            if (xmlFile !is XmlFile) {
                return null
            }
            return DomManager.getDomManager(project)
                .getFileElement(xmlFile, rootClass)
        }
    }

    fun tryConvert(
        project: Project,
        psiFile: PsiFile,
        mapping: IdentityHashMap<Node, ActualCodeElement>
    ): List<Node>?
}
