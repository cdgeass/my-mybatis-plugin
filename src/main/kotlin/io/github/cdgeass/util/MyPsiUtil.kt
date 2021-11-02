package io.github.cdgeass.util

import com.intellij.psi.*
import com.intellij.psi.impl.light.LightMethodBuilder
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

fun resolveLombokField(psiField: PsiField): PsiElement {
    return resolveLombokFields(listOf(psiField)).first()
}

fun resolveLombokFields(psiFields: Collection<PsiField>): Collection<PsiElement> {
    return psiFields.map { psiField ->
        val fieldName = psiField.name
        // TODO boolean
        val getMethodName = "get" + fieldName[0].uppercaseChar() + fieldName.substring(1)
        val psiClass = psiField.parent as PsiClass
        val lombokGetMethod = psiClass.allMethods.find { method ->
            method.name == getMethodName && method is LightMethodBuilder
        }
        lombokGetMethod ?: psiField
    }
}