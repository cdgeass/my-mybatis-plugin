package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.*
import com.intellij.util.xml.DomElement
import io.github.cdgeass.codeInsight.util.getIdentifyElement

/**
 * @author cdgeass
 * @since 2021/3/20
 */
class MyDomElementReference(
    psiElement: PsiElement,
    private val domElements: Array<DomElement>
) : PsiReferenceBase<PsiElement>(psiElement), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val results = multiResolve(true)
        if (results.isEmpty()) {
            return null
        }
        return results[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return domElements.mapNotNull { getIdentifyElement(it) }
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

}