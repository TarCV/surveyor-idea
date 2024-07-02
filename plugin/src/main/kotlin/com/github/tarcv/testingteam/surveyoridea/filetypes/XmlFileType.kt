/*
 *  Copyright (C) 2024 TarCV
 *
 *  This file is part of UI Surveyor.
 *  UI Surveyor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.tarcv.testingteam.surveyoridea.filetypes

import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomFileElement
import com.intellij.util.xml.DomManager
import java.util.IdentityHashMap
import javax.swing.Icon

sealed interface XmlFileType {
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

        fun structureIconFor(tag: XmlTag): Icon {
            return when {
                tag.subTags.isEmpty() -> AllIcons.Ide.LocalScope
                else -> AllIcons.Actions.GroupBy
            }
        }

        fun rootTagFrom(o: XmlTag) = (o.containingFile as? XmlFile)?.rootTag
    }

    fun tryConvert(
        project: Project,
        psiFile: PsiFile,
        mapping: IdentityHashMap<Node, UiPsiElementReference>
    ): List<Node>?
}
