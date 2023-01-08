package com.github.tarcv.surveyoridea.filetypes.uix

import com.github.tarcv.surveyoridea.filetypes.ActualUiElement
import com.github.tarcv.surveyoridea.filetypes.RootUiElement
import com.intellij.icons.AllIcons
import com.intellij.ide.presentation.Presentation
import com.intellij.ide.presentation.PresentationProvider
import com.intellij.util.xml.*
import java.lang.ref.WeakReference
import javax.swing.Icon

class UixDomDecription: DomFileDescription<Hierarchy>(Hierarchy::class.java, "hierarchy") {
    override fun getVersion(): Int {
        return super.getVersion()
    }

    override fun getStubVersion(): Int {
        return super.getStubVersion()
    }
}

interface Hierarchy: RootUiElement {
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
