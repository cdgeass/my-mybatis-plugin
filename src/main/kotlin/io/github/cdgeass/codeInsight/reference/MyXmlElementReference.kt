package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import io.github.cdgeass.codeInsight.util.getPsiElement
import io.github.cdgeass.codeInsight.util.resolveElementReferences

/**
 * @author cdgeass
 * @since 2021/3/25
 */
class MyXmlElementReference(element: PsiElement) : PsiPolyVariantReferenceBase<PsiElement>(element) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return PsiElementResolveResult.createResults(
            resolveElementReferences(myElement).mapNotNull { getPsiElement(it) }
        )
    }

    override fun getVariants(): Array<Any> {
        return resolveElementReferences(myElement, true).mapNotNull { getPsiElement(it) }.toTypedArray()
    }
}
