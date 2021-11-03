package io.github.cdgeass.util

import com.intellij.codeInsight.completion.JavaLookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
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

fun createArrayClass(project: Project): PsiClass {
    val psiElementFactory = PsiElementFactory.getInstance(project)
    return psiElementFactory.createClass("array")
}

fun createIntClass(project: Project): PsiClass {
    val psiElementFactory = PsiElementFactory.getInstance(project)
    return psiElementFactory.createClass("int")
}

fun createLookupElement(name: String, element: PsiElement): LookupElement {
    return when (element) {
        is PsiField -> {
            JavaLookupElementBuilder.forField(element, name, null)
        }
        is LightMethodBuilder -> {
            LookupElementBuilder.create(element, name)
                .withIcon(element.getIcon(Iconable.ICON_FLAG_VISIBILITY))
        }
        is PsiMethod -> {
            JavaLookupElementBuilder.forMethod(element, name, PsiSubstitutor.EMPTY, null)
        }
        else -> {
            LookupElementBuilder.create(element, name)
                .withIcon(AllIcons.Gutter.ExtAnnotation)
        }
    }
}