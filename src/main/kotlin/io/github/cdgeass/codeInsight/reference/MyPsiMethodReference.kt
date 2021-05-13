package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.DomUtil
import io.github.cdgeass.codeInsight.dom.element.Mapper

/**
 * @author cdgeass
 * @since 2021/3/26
 */
class MyPsiMethodReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement? {
        val psiMethods = resolvePsiMethods(element)
        return if (psiMethods.isEmpty()) {
            null
        } else {
            psiMethods[0]
        }
    }

    override fun getVariants(): Array<Any> {
        return resolvePsiMethods(myElement, true).toTypedArray()
    }
}

fun resolvePsiMethods(element: PsiElement, ignored: Boolean = false): List<PsiMethod> {
    if (element !is XmlAttributeValue) return emptyList()

    val mapper = DomUtil.findDomElement(element, Mapper::class.java) ?: return emptyList()
    val psiClass = mapper.getNamespace().value ?: return emptyList()

    val name = element.value
    return psiClass.allMethods.filter { ignored || it.name == name }
}
