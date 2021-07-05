package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReference
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.reference.MyPsiMethodReference

/**
 * @author cdgeass
 * @since 2021-03-18
 */
class MyPsiMethodConverter : Converter<PsiMethod>(), CustomReferenceConverter<PsiMethod> {

    override fun toString(t: PsiMethod?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiMethod? {
        if (s.isNullOrBlank() || context?.referenceXmlElement == null) {
            return null
        }

        val psiMethods = resolvePsiMethods(context.referenceXmlElement!!)
        return if (psiMethods.isEmpty()) {
            null
        } else {
            psiMethods[0]
        }
    }

    override fun createReferences(
        value: GenericDomValue<PsiMethod>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null || value?.value == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(MyPsiMethodReference(element, value.value!!))
    }

    private fun resolvePsiMethods(element: PsiElement, ignored: Boolean = false): List<PsiMethod> {
        if (element !is XmlAttributeValue) return emptyList()

        val mapper = DomUtil.findDomElement(element, Mapper::class.java) ?: return emptyList()
        val psiClass = mapper.getNamespace().value ?: return emptyList()

        val name = element.value
        return psiClass.allMethods.filter { ignored || it.name == name }
    }
}
