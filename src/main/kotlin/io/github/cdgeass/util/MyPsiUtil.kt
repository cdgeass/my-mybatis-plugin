package io.github.cdgeass.util

import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.jetbrains.rd.util.first

/**
 * @author cdgeass
 * @since 2021-04-01
 */
fun resolveGeneric(psiType: PsiType): PsiClass? {
    if (psiType is PsiClassType) {
        val substitutionMap = psiType.resolveGenerics().substitutor.substitutionMap
        if (substitutionMap.isEmpty()) return null
        val genericType = substitutionMap.first().value
        if (genericType is PsiClassType) {
            return genericType.resolve()
        }
    } else if (psiType is PsiArrayType) {
        return null
    }
    return null
}
