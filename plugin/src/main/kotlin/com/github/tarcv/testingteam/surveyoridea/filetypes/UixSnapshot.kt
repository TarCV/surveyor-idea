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
import com.github.tarcv.testingteam.surveyor.Property
import com.github.tarcv.testingteam.surveyoridea.filetypes.XmlFileType.Companion.tryReadAsXml
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.RootUiElement
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.ActualUiElement
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.UiPsiElementReference
import com.intellij.icons.AllIcons
import com.intellij.ide.presentation.Presentation
import com.intellij.ide.presentation.PresentationProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.DomFileDescription
import com.intellij.util.xml.ElementPresentationManager
import com.intellij.util.xml.GenericAttributeValue
import java.lang.ref.WeakReference
import java.util.IdentityHashMap
import javax.swing.Icon

@Suppress("unused")
object UixSnapshot: XmlFileType {
    override fun tryConvert(
        project: Project,
        psiFile: PsiFile,
        mapping: IdentityHashMap<com.github.tarcv.testingteam.surveyor.Node, UiPsiElementReference>
    ): List<com.github.tarcv.testingteam.surveyor.Node>? {
        val uix = tryReadAsXml(project, psiFile, rootElementClass)
            ?: return null
        return uix.rootElement.nodes
            .map { convert(it, mapping) }
    }

    fun isXmlFileOfType(file: XmlFile): Boolean {
        return tryReadAsXml(file.project, file, rootElementClass) != null
    }

    private fun convert(
        node: Node,
        mapping: IdentityHashMap<com.github.tarcv.testingteam.surveyor.Node, UiPsiElementReference>
    ): com.github.tarcv.testingteam.surveyor.Node {
        val props: Map<Property<*>, Any?> = listOf(
            DroidProperty.IS_CHECKABLE to node.checkable.value,
            DroidProperty.IS_CHECKED to node.checked.value,
            DroidProperty.CLASS_NAME to node.clazz.value,
            DroidProperty.IS_CLICKABLE to node.clickable.value,
            DroidProperty.ACCESSIBILITY_DESCRIPTION to node.contentDesc.value,
            DroidProperty.IS_ENABLED to node.enabled.value,
            DroidProperty.IS_FOCUSABLE to node.focusable.value,
            DroidProperty.IS_FOCUSED to node.focused.value,
            DroidProperty.IS_LONG_CLICKABLE to node.longClickable.value,
            DroidProperty.PACKAGE_NAME to node.`package`.value,
            DroidProperty.IS_PASSWORD_FIELD to node.password.value,
            DroidProperty.RESOURCE_ID to node.resourceId.value,
            DroidProperty.IS_SCROLLABLE to node.scrollable.value,
            DroidProperty.IS_SELECTED to node.selected.value,
            DroidProperty.TEXT to node.text.value,
        ).filter { it.second != null }.toMap()

        val out = com.github.tarcv.testingteam.surveyor.Node(
            null,
            props,
            node.nodes.map { convert(it, mapping) },
            true // TODO
        ).apply {
            finalizeChildren()
        }

        mapping[out] = node

        return out
    }
}

class UixDomDecription: DomFileDescription<Hierarchy>(rootElementClass, "hierarchy")

@Presentation(provider = RootUiElement.DescriptionProvider::class)
interface Hierarchy: RootUiElement {
    @Suppress("unused")
    val rotation: GenericAttributeValue<Int>

    val nodes: List<Node>
}

@Presentation(provider = Node.DescriptionProvider::class)
interface Node: ActualUiElement {
    val nodes: List<Node>

    val index: GenericAttributeValue<Int>
    val text: GenericAttributeValue<String>
    @get:Attribute("resource-id") val resourceId: GenericAttributeValue<String>
    @get:Attribute("class") val clazz: GenericAttributeValue<String>
    val `package`: GenericAttributeValue<String>
    @get:Attribute("content-desc") val contentDesc: GenericAttributeValue<String>
    val checkable: GenericAttributeValue<Boolean>
    val checked: GenericAttributeValue<Boolean>
    val clickable: GenericAttributeValue<Boolean>
    val enabled: GenericAttributeValue<Boolean>
    val focusable: GenericAttributeValue<Boolean>
    val focused: GenericAttributeValue<Boolean>
    val scrollable: GenericAttributeValue<Boolean>
    @get:Attribute("long-clickable") val longClickable: GenericAttributeValue<Boolean>
    val password: GenericAttributeValue<Boolean>
    val selected: GenericAttributeValue<Boolean>
    val bounds: GenericAttributeValue<String>

    override val psiElement: PsiElement?
        get() = super.psiElement

    class DescriptionProvider : PresentationProvider<Node>() {
        companion object {
            private val typeIcons = mutableMapOf<String, WeakReference<Icon>>()
        }

        override fun getName(t: Node?): String? {
            if (t == null) {
                return null
            }

            return buildString {
                append(
                    t.clazz.value?.substringAfterLast('.') ?: "View"
                )
                append(' ')
                append(
                    t.resourceId.value?.substringAfterLast(":id/")
                        ?: t.contentDesc.value?.let { "'$it'" }
                        ?: t.text.value?.let { "\"$it\"" }
                )
            }
        }

        override fun getIcon(t: Node?): Icon? {
            if (t == null) {
                return null
            }

            val typeName = t.clazz.value
            typeName
                ?.let { typeIcons[it] }
                ?.get()
                ?.let { return it }

            val result = typeName
                ?.let {
                    try {
                        Class.forName(it, false, this::class.java.classLoader)
                    } catch (e: Throwable) {
                        null
                    }
                }
                ?.let {
                    ElementPresentationManager.getIconForClass(it)
                }
                ?: when {
                    t.nodes.isEmpty() -> AllIcons.Ide.LocalScope
                    else -> AllIcons.Actions.GroupBy
                }

            if (typeName != null) {
                typeIcons[typeName] = WeakReference(result)
            }

            return result
        }
    }
}

private val rootElementClass = Hierarchy::class.java
