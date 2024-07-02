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
package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.filetypes.IAutSnapshot
import com.github.tarcv.testingteam.surveyoridea.filetypes.XmlFileType
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.ActualUiElement
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.RootUiElement
import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.util.Iconable
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomManager
import javax.swing.Icon

class UiAwareNavigationBar : StructureAwareNavBarModelExtension() {
    override val language: Language = XMLLanguage.INSTANCE

    override fun getPresentableText(o: Any?): String? {
        if (o is XmlFile) {
            return o.containingFile.virtualFile?.presentableName ?: o.containingFile.name
        }
        if (o !is XmlTag) {
            return null
        }

        val domElement = DomManager.getDomManager(o.project)
            .getDomElement(o)
        if (domElement == null) {
            val rootTag = XmlFileType.rootTagFrom(o) ?: return null
            return if (IAutSnapshot.isXmlFileOfType(rootTag)) {
                IAutSnapshot.structureTitleFor(o)
            } else {
                null
            }
        } else {
            return when (domElement) {
                is RootUiElement -> o.containingFile.virtualFile?.presentableName ?: o.containingFile.name
                is ActualUiElement -> domElement.presentation.elementName
                else -> null
            }
        }
    }

    override fun getIcon(o: Any?): Icon? = when (o) {
        is XmlFile -> o.containingFile.getIcon(Iconable.ICON_FLAG_VISIBILITY)
        is XmlTag -> XmlFileType.structureIconFor(o)
        else -> null
    }
}