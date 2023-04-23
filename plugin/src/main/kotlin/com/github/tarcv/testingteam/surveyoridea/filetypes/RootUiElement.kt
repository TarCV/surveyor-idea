/*
 *  Copyright (C) 2023 TarCV
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

import com.intellij.ide.presentation.PresentationProvider
import com.intellij.openapi.util.Iconable
import com.intellij.util.xml.DomElement
import javax.swing.Icon

/**
 * DomElement not representing any actual UI item in a UI dump, and instead representing a root of a dump instead
 */
interface RootUiElement: DomElement {
    class DescriptionProvider : PresentationProvider<RootUiElement>() {
        override fun getName(t: RootUiElement?): String? {
            return t?.xmlTag
                ?.containingFile
                ?.virtualFile
                ?.presentableName
                ?: t?.xmlTag?.containingFile?.name
        }

        override fun getIcon(t: RootUiElement?): Icon? {
            return t?.xmlTag
                ?.containingFile
                ?.getIcon(Iconable.ICON_FLAG_VISIBILITY)
        }
    }
}
