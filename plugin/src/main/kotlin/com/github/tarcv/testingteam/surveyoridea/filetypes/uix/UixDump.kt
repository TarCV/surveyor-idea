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
package com.github.tarcv.testingteam.surveyoridea.filetypes.uix

import com.github.tarcv.testingteam.surveyoridea.filetypes.ActualUiElement
import com.github.tarcv.testingteam.surveyoridea.filetypes.RootUiElement
import com.intellij.icons.AllIcons
import com.intellij.ide.highlighter.DomSupportEnabled
import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.ide.presentation.Presentation
import com.intellij.ide.presentation.PresentationProvider
import com.intellij.lang.xml.XMLLanguage
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.DomFileDescription
import com.intellij.util.xml.ElementPresentationManager
import com.intellij.util.xml.GenericAttributeValue
import java.lang.ref.WeakReference
import javax.swing.Icon

class UixDomDecription: DomFileDescription<Hierarchy>(Hierarchy::class.java, "hierarchy")

class UixFileType : XmlLikeFileType(XMLLanguage.INSTANCE), DomSupportEnabled {
    override fun getName(): String = UixFileType::class.java.name

    override fun getDescription(): String = "UI Automator hierarchy XML"

    override fun getDefaultExtension(): String = "uix"

    override fun getIcon(): Icon = AllIcons.FileTypes.Xml
}

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
