package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.light.LightMethodBuilder
import java.util.*

/**
 * @author cdgeass
 * @since 2021/3/26
 */
class MyPsiFieldReference(
    element: PsiElement,
    private val myField: PsiField
) : PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement {
        return myField
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return when (element) {
            is LightMethodBuilder -> {
                element.navigationElement == myField
            }
            is PsiMethod -> {
                isGetter(element, myField)
            }
            else -> {
                false
            }
        }
    }

    private fun isGetter(method: PsiMethod, field: PsiField): Boolean {
        if (!method.parameterList.isEmpty) {
            return false
        }
        if (method.returnType != field.type) {
            return false
        }
        val methodName = method.name
        val fieldName = field.name
        return when {
            methodName.startsWith("is") -> {
                methodName.substring(2).lowercase(Locale.getDefault()) == fieldName
            }
            methodName.startsWith("get") -> {
                methodName.substring(3).lowercase(Locale.getDefault()) == fieldName
            }
            else -> {
                false
            }
        }
    }

    override fun isSoft(): Boolean {
        return true
    }
}