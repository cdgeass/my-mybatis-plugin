package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.*
import com.intellij.psi.impl.light.LightMethodBuilder

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
                isGetter(element, myField) || isSetter(element, myField)
            }
            else -> {
                false
            }
        }
    }

    private fun isSetter(method: PsiMethod, field: PsiField): Boolean {
        if (method.parameterList.isEmpty) {
            return false
        }
        if (method.returnType != PsiTypes.voidType()) {
            return false
        }
        val methodName = method.name
        val fieldName = field.name
        return when {
            methodName.startsWith("set") -> {
                substring(methodName, 3) == fieldName
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
                substring(methodName, 2) == fieldName
            }
            methodName.startsWith("get") -> {
                substring(methodName, 3) == fieldName
            }
            else -> {
                false
            }
        }
    }

    private fun substring(str: String, length: Int): String {
        val substring = str.substring(length)
        val firstChar = substring[0]
        return substring.replaceFirst(firstChar, firstChar.lowercase()[0])
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return super.handleElementRename(
            when {
                newElementName.startsWith("is") -> {
                    substring(newElementName, 2)
                }
                newElementName.startsWith("get") -> {
                    substring(newElementName, 3)
                }
                newElementName.startsWith("set") -> {
                    substring(newElementName, 3)
                }
                else -> {
                    newElementName
                }
            }
        )
    }

    override fun isSoft(): Boolean {
        return true
    }
}
