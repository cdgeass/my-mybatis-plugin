package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReferenceBase

/**
 * @author cdgeass
 * @since 2021/3/26
 */
class MyPsiMethodReference(
    element: PsiElement,
    private val myMethod: PsiMethod
) : PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement {
        return myMethod
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return element == myElement
    }

    override fun isSoft(): Boolean {
        return true
    }
}
