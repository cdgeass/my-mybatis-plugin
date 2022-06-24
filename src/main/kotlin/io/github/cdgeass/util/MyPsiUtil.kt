package io.github.cdgeass.util

import com.intellij.codeInsight.completion.JavaLookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightMethodBuilder

/**
 * @author cdgeass
 * @since 2021-04-01
 */
fun createArrayClass(project: Project): PsiClass {
    val psiElementFactory = PsiElementFactory.getInstance(project)
    return psiElementFactory.createClass("array")
}

fun createIntClass(project: Project): PsiClass {
    val psiElementFactory = PsiElementFactory.getInstance(project)
    return psiElementFactory.createClass("X")
}

// 是否是指定限定名的子类
fun isSub(psiClass: PsiClass, superClassNames: Array<String>): Boolean {
    if (psiClass.supers.isNotEmpty()) {
        return if (psiClass.supers.any { superClassNames.contains(it.qualifiedName) }) {
            true
        } else {
            psiClass.supers.any { isSub(it, superClassNames) }
        }
    }
    return superClassNames.contains(psiClass.qualifiedName)
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