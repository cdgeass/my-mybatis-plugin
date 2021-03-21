package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult

/**
 * @author cdgeass
 * @since 2021/3/18
 */
class MyPsiElementReference(
    psiElement: PsiElement,
    private val targets: List<PsiElement>?
) : PsiPolyVariantReferenceBase<PsiElement>(psiElement) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        if (targets.isNullOrEmpty()) {
            return emptyArray()
        }
        return PsiElementResolveResult.createResults(targets)
    }

}