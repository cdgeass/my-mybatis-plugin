package io.github.cdgeass.codeInsight.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaResolveResult
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiSubstitutor
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceSet
import com.intellij.psi.infos.ClassCandidateInfo

/**
 * @author cdgeass
 * @since 2021-06-29
 */
class MyJavaClassReference(
    referenceSet: JavaClassReferenceSet,
    range: TextRange?,
    index: Int,
    val text: String?,
    staticImport: Boolean,
    val psiClass: PsiClass
) : JavaClassReference(referenceSet, range, index, text, staticImport) {

    override fun advancedResolve(incompleteCode: Boolean): JavaResolveResult {
        return if (text?.contains(".") != true) {
            ClassCandidateInfo(psiClass, PsiSubstitutor.EMPTY, false, element)
        } else {
            super.advancedResolve(incompleteCode)
        }
    }
}
