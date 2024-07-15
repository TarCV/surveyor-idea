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

import com.github.tarcv.testingteam.surveyor.DroidProperty
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.intellij.psi.xml.XmlTag

object UixSnapshot: XmlFileType<UixNode, DroidProperty<*>>(
    rootTagName = "hierarchy",
    propertyClass = DroidProperty::class
) {

    override fun uiNodeFactory(node: XmlTag) = UixNode(node)

    override fun propertyFactory(
        it: DroidProperty<*>,
        node: XmlTag
    ) = when (it) {
        is DroidProperty.IS_CHECKABLE -> getPropertyFromTag(it, node, "checkable")
        is DroidProperty.IS_CHECKED -> getPropertyFromTag(it, node, "checked")
        is DroidProperty.CLASS_NAME -> getPropertyFromTag(it, node, "class")
        is DroidProperty.IS_CLICKABLE -> getPropertyFromTag(it, node, "clickable")
        is DroidProperty.ACCESSIBILITY_DESCRIPTION -> getPropertyFromTag(it, node, "content-desc")
        is DroidProperty.IS_ENABLED -> getPropertyFromTag(it, node, "enabled")
        is DroidProperty.IS_FOCUSABLE -> getPropertyFromTag(it, node, "focusable")
        is DroidProperty.IS_FOCUSED -> getPropertyFromTag(it, node, "focused")
        is DroidProperty.IS_LONG_CLICKABLE -> getPropertyFromTag(it, node, "long-clickable")
        is DroidProperty.PACKAGE_NAME -> getPropertyFromTag(it, node, "package")
        is DroidProperty.IS_PASSWORD_FIELD -> getPropertyFromTag(it, node, "password")
        is DroidProperty.RESOURCE_ID -> getPropertyFromTag(it, node, "resource-id")
        is DroidProperty.IS_SCROLLABLE -> getPropertyFromTag(it, node, "scrollable")
        is DroidProperty.IS_SELECTED -> getPropertyFromTag(it, node, "selected")
        is DroidProperty.TEXT -> getPropertyFromTag(it, node, "text")
    }

    override fun structureTitleFor(tag: XmlTag): String = buildString {
        append(
            tag.getAttribute("class")?.value
                ?.substringAfterLast('.')
                ?: "View"
        )
        append(' ')
        append(
            tag.getAttribute("resource-id")?.value
                ?.substringAfterLast(":id/")
                ?: tag.getAttribute("content-desc")?.value?.let { "'$it'" }
                ?: tag.getAttribute("text")?.value?.let { "\"$it\"" }
        )
    }
}

data class UixNode(override val psiElement: XmlTag): UiPsiElementReference