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
import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import java.util.IdentityHashMap

data object IAutSnapshot: XmlFileType {
    override fun tryConvert(
        project: Project,
        psiFile: PsiFile,
        mapping: IdentityHashMap<Node, UiPsiElementReference>
    ): List<Node>? {
        if (psiFile !is XmlFile) {
            return null
        }

        val rootTag = psiFile.rootTag ?: return null
        if (!isXmlFileOfType(rootTag)) {
            return null
        }
        return rootTag.subTags
            .map { convert(it, mapping) }
    }

    fun isXmlFileOfType(rootTag: XmlTag): Boolean {
        return rootTag.name == "AppiumAUT"
    }

    private fun convert(
        node: XmlTag,
        mapping: IdentityHashMap<Node, UiPsiElementReference>
    ): Node {
        // TODO: How to handle missing values?
        val props: Map<IProperty<*>, Any?> = IProperty.allProperties
            .associateWith {
                when (it) {
                    IProperty.CHILD_INDEX -> getPropertyFromTag(IProperty.CHILD_INDEX, node, "index")
                    IProperty.ELEMENT_TYPE -> getPropertyFromTag(IProperty.ELEMENT_TYPE, node, "type")
                    IProperty.HAS_FOCUS -> getPropertyFromTag(IProperty.HAS_FOCUS, node, "focused")
                    IProperty.IDENTIFIER_OR_LABEL -> getPropertyFromTag(IProperty.IDENTIFIER_OR_LABEL, node, "name")
                    IProperty.IS_ACCESSIBILITY_CONTAINER -> getPropertyFromTag(IProperty.IS_ACCESSIBILITY_CONTAINER, node, "accessibilityContainer")
                    IProperty.IS_ACCESSIBLE -> getPropertyFromTag(IProperty.IS_ACCESSIBLE, node, "accessible")
                    IProperty.IS_ENABLED -> getPropertyFromTag(IProperty.IS_ENABLED, node, "enabled")
                    IProperty.IS_SELECTED -> getPropertyFromTag(IProperty.IS_SELECTED, node, "selected")
                    IProperty.LABEL -> getPropertyFromTag(IProperty.LABEL, node, "label")
                    IProperty.VALUE -> getPropertyFromTag(IProperty.VALUE, node, "value")
                    IProperty.WDA_UID -> getPropertyFromTag(IProperty.WDA_UID, node, "uid")
                    IProperty.X -> getPropertyFromTag(IProperty.X, node, "x")
                    IProperty.Y -> getPropertyFromTag(IProperty.Y, node, "y")
                    IProperty.WIDTH -> getPropertyFromTag(IProperty.WIDTH, node, "width")
                    IProperty.HEIGHT -> getPropertyFromTag(IProperty.HEIGHT, node, "height")
                }
            }

        val out = Node(
            null,
            props,
            node.subTags.map { convert(it, mapping) },
            true // TODO
        ).apply {
            finalizeChildren()
        }

        mapping[out] = IAutNode(node)

        return out
    }

    fun structureTitleFor(tag: XmlTag): String = buildString {
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
@Suppress("UNUSED_PARAMETER")
private fun getPropertyFromTag(property: IProperty<out Boolean?>, node: XmlTag, attribute: String): Boolean? {
    return node.getAttribute(attribute)
        ?.value
        ?.toBooleanStrictOrNull()
}
@Suppress("UNUSED_PARAMETER")
private fun getPropertyFromTag(property: IProperty<out Int?>, node: XmlTag, attribute: String): Int? {
    return node.getAttribute(attribute)
        ?.value
        ?.toIntOrNull()
}
@Suppress("UNUSED_PARAMETER")
private fun getPropertyFromTag(property: IProperty<out String?>, node: XmlTag, attribute: String): String? {
    return node.getAttribute(attribute)?.value
}

data class IAutNode(override val psiElement: XmlTag): UiPsiElementReference
