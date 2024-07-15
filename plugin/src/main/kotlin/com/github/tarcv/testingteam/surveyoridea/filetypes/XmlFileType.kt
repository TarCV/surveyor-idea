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
import com.github.tarcv.testingteam.surveyor.Property
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomFileElement
import com.intellij.util.xml.DomManager
import java.util.IdentityHashMap
import javax.swing.Icon
import kotlin.reflect.KClass

sealed class XmlFileType<N: UiPsiElementReference, P : Property<*>>(
    private val rootTagName: String,
    private val propertyClass: KClass<P>
) {

    companion object {
        fun structureIconFor(tag: XmlTag): Icon {
            return when {
                tag.subTags.isEmpty() -> AllIcons.Ide.LocalScope
                else -> AllIcons.Actions.GroupBy
            }
        }

        fun rootTagFrom(o: PsiElement?) = (o?.containingFile as? XmlFile)?.rootTag
    }

    fun tryConvert(
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

    abstract fun uiNodeFactory(node: XmlTag): N
    abstract fun propertyFactory(it: P, node: XmlTag): Any?
    abstract fun structureTitleFor(tag: XmlTag): String
    fun isXmlFileOfType(rootTag: XmlTag): Boolean {
        return rootTag.name == rootTagName
    }

    private fun convert(
        node: XmlTag,
        mapping: IdentityHashMap<Node, UiPsiElementReference>
    ): Node {
        val props: Map<P, Any?> = propertyClass.sealedSubclasses
            .mapNotNull {
                it.objectInstance
            }
            .associateWith {
                propertyFactory(it, node)
            }

        val out = Node(
            null,
            props,
            node.subTags.map { convert(it, mapping) },
            true // TODO
        ).apply {
            finalizeChildren()
        }

        mapping[out] = uiNodeFactory(node)

        return out
    }
}
