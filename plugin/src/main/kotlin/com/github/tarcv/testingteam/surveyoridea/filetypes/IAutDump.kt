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

import com.github.tarcv.testingteam.surveyor.IProperty
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.intellij.psi.xml.XmlTag

object IAutSnapshot: XmlFileType<IAutNode, IProperty<*>>(
    rootTagName = "AppiumAUT",
    propertyClass = IProperty::class
) {

    override fun uiNodeFactory(node: XmlTag) = IAutNode(node)

    override fun propertyFactory(
        it: IProperty<*>,
        node: XmlTag
    ) = when (it) {
        is IProperty.CHILD_INDEX -> getPropertyFromTag(it, node, "index")
        is IProperty.ELEMENT_TYPE -> getPropertyFromTag(it, node, "type")
        is IProperty.HAS_FOCUS -> getPropertyFromTag(it, node, "focused")
        is IProperty.IDENTIFIER_OR_LABEL -> getPropertyFromTag(it, node, "name")
        is IProperty.IS_ACCESSIBILITY_CONTAINER -> getPropertyFromTag(it, node, "accessibilityContainer")
        is IProperty.IS_ACCESSIBLE -> getPropertyFromTag(it, node, "accessible")
        is IProperty.IS_ENABLED -> getPropertyFromTag(it, node, "enabled")
        is IProperty.IS_SELECTED -> getPropertyFromTag(it, node, "selected")
        is IProperty.LABEL -> getPropertyFromTag(it, node, "label")
        is IProperty.VALUE -> getPropertyFromTag(it, node, "value")
        is IProperty.WDA_UID -> getPropertyFromTag(it, node, "uid")
        is IProperty.X -> getPropertyFromTag(it, node, "x")
        is IProperty.Y -> getPropertyFromTag(it, node, "y")
        is IProperty.WIDTH -> getPropertyFromTag(it, node, "width")
        is IProperty.HEIGHT -> getPropertyFromTag(it, node, "height")
    }

    override fun structureTitleFor(tag: XmlTag): String = buildString {
        append(tag.name.removePrefix("XCUIElementType"))
        append(' ')
        append(
            tag.getAttribute("name")?.value
                ?: tag.getAttribute("label")?.value?.let { "'$it'" }
                ?: tag.getAttribute("value")?.value?.let { "\"$it\"" }
                ?: ""
        )
    }
}

data class IAutNode(override val psiElement: XmlTag): UiPsiElementReference