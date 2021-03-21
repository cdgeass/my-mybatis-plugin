package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.util.xml.DomElement
import io.github.cdgeass.codeInsight.util.getIdentifyElement

/**
 * @author cdgeass
 * @since 2021/3/20
 */
class MyDomElementReference(
    psiElement: PsiElement,
    private val domElements: Array<DomElement>
) : PsiPolyVariantReferenceBase<PsiElement>(psiElement) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return PsiElementResolveResult.createResults(domElements.mapNotNull { getIdentifyElement(it) })
    }

}