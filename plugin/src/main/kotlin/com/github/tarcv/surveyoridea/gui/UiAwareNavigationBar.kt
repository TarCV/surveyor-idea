package com.github.tarcv.surveyoridea.gui

import com.github.tarcv.surveyoridea.filetypes.ActualUiElement
import com.github.tarcv.surveyoridea.filetypes.RootUiElement
import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomManager
import javax.swing.Icon

class UiAwareNavigationBar : StructureAwareNavBarModelExtension() {
    override val language: Language
        get() = XMLLanguage.INSTANCE

    override fun getPresentableText(o: Any?): String? {
        if (o !is XmlTag) {
            return null
        }

        val domElement = DomManager.getDomManager(o.project)
            .getDomElement(o)
        return when (domElement) {
            is RootUiElement -> o.containingFile.virtualFile?.presentableName ?: o.containingFile.name
            is ActualUiElement -> domElement.presentation.elementName
            else -> null
        }
    }

    override fun getIcon(o: Any?): Icon? {
        if (o !is XmlTag) {
            return null
        }

        val domElement = DomManager.getDomManager(o.project)
            .getDomElement(o)
        return when (domElement) {
            is RootUiElement -> o.containingFile.getIcon(ICON_FLAG_VISIBILITY)
            is ActualUiElement -> domElement.presentation.icon
            else -> null
        }
    }
}