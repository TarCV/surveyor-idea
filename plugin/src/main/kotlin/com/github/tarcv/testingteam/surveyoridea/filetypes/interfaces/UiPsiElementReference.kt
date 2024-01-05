package com.github.tarcv.testingteam.surveyoridea.filetypes.interfaces

import com.intellij.psi.PsiElement

/**
 * Object holding reference to a UI element representation (which is a PsiElement) in a UI snapshot
 */
interface UiPsiElementReference {
    val psiElement: PsiElement?
}