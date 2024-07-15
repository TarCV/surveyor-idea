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
package com.github.tarcv.testingteam.surveyoridea.filetypes.structure

import com.github.tarcv.testingteam.surveyoridea.filetypes.XmlFileType
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.structureView.xml.XmlStructureViewBuilderProvider
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import javax.swing.Icon

abstract class SnapshotStructureViewFactory(private val snapshotType: XmlFileType<*, *>)
    : XmlStructureViewBuilderProvider {
    override fun createStructureViewBuilder(file: XmlFile): StructureViewBuilder? {
        val rootTag = file.rootTag ?: return null
        if (!snapshotType.isXmlFileOfType(rootTag)) {
            return null
        }
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return SnapshotStructureViewModel(editor, file, snapshotType)
            }
        }
    }
}
class SnapshotStructureViewModel(editor: Editor?, private val psiFile: XmlFile, private val snapshotType: XmlFileType<*, *>)
    : TextEditorBasedStructureViewModel(editor, psiFile), StructureViewModel.ElementInfoProvider {
    companion object {
        val classes: Array<Class<*>> = arrayOf(XmlTag::class.java)
    }

    override fun getRoot(): StructureViewTreeElement = SnapshotStructureRoot(snapshotType, psiFile)

    override fun shouldEnterElement(element: Any?): Boolean {
        return element is XmlTag && element.subTags.isNotEmpty()
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean = false

    override fun getSuitableClasses(): Array<Class<*>> = classes
}

class SnapshotStructureRoot(
    private val snapshotType: XmlFileType<*, *>,
    private val file: XmlFile
) : PsiTreeElementBase<XmlFile>(file) {
    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        return file.rootTag?.children
            ?.mapNotNull {
                val tag = it as? XmlTag ?: return@mapNotNull null
                SnapshotStructureViewElement(snapshotType, tag)
            }
            ?: emptyList()
    }

    override fun getPresentableText(): String = file.name
}

class SnapshotStructureViewElement(private val snapshotType: XmlFileType<*, *>, private val element: XmlTag) : PsiTreeElementBase<XmlTag>(element) {
    private val _presentation by lazy {
        val tag = element as? XmlTag ?: return@lazy PresentationData()
        StructureItemPresentation(snapshotType, tag)
    }

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        return element.subTags
            .map {
                SnapshotStructureViewElement(snapshotType, it)
            }
    }

    override fun getPresentation(): ItemPresentation = _presentation

    override fun getPresentableText(): String? = _presentation.presentableText
}

class StructureItemPresentation(
    private val snapshotType: XmlFileType<*, *>,
    private val tag: XmlTag
) : ItemPresentation {
    override fun getPresentableText(): String = snapshotType.structureTitleFor(tag)
    override fun getIcon(unused: Boolean): Icon = XmlFileType.structureIconFor(tag)
}