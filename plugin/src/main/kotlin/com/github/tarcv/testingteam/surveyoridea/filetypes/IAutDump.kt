package com.github.tarcv.testingteam.surveyoridea.filetypes

import com.github.tarcv.testingteam.surveyor.IProperty
import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces.ActualCodeElement
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import java.util.IdentityHashMap

object IAutSnapshot: FileType {
    override fun tryConvert(
        project: Project,
        psiFile: PsiFile,
        mapping: IdentityHashMap<Node, ActualCodeElement>
    ): List<Node>? {
        if (psiFile !is XmlFile) {
            return null
        }

        val rootTag = psiFile.rootTag
        if (rootTag == null || rootTag.name != "AppiumAUT") {
            return null
        }
        return rootTag.subTags
            .map { convert(it, mapping) }
    }

    private fun convert(
        node: XmlTag,
        mapping: IdentityHashMap<Node, ActualCodeElement>
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
}
private fun getPropertyFromTag(property: IProperty<out Boolean?>, node: XmlTag, attribute: String): Boolean? {
    return node.getAttribute(attribute)
        ?.value
        ?.toBooleanStrictOrNull()
}
private fun getPropertyFromTag(property: IProperty<out Int?>, node: XmlTag, attribute: String): Int? {
    return node.getAttribute(attribute)
        ?.value
        ?.toIntOrNull()
}
private fun getPropertyFromTag(property: IProperty<out String?>, node: XmlTag, attribute: String): @NlsSafe String? {
    return node.getAttribute(attribute)?.value
}

data class IAutNode(override val psiElement: XmlTag): ActualCodeElement
