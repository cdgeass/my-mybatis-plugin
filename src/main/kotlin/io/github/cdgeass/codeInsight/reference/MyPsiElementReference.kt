package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.*

/**
 * @author cdgeass
 * @since 2021/3/18
 */
class MyPsiElementReference(
    psiElement: PsiElement,
    private val targets: List<PsiElement>?
) : PsiReferenceBase<PsiElement>(psiElement), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val results = multiResolve(true)
        if (results.isEmpty()) {
            return null
        }
        return results[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        if (targets.isNullOrEmpty()) {
            return emptyArray()
        }
        return targets.map { PsiElementResolveResult(it) }.toTypedArray()
    }

}