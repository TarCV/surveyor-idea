package com.github.tarcv.surveyoridea.filetypes.uix

import com.intellij.util.xml.Attribute
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomFileDescription
import com.intellij.util.xml.GenericAttributeValue

class UixDomDecription: DomFileDescription<Hierarchy>(Hierarchy::class.java, "hierarchy") {
    override fun getVersion(): Int {
        return super.getVersion()
    }

    override fun getStubVersion(): Int {
        return super.getStubVersion()
    }
}

interface Hierarchy: DomElement {
    val rotation: GenericAttributeValue<Int>

    val nodes: List<Node>
}

interface Node: DomElement {
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
}
