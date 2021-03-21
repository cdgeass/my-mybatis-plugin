package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReference
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.dom.reference.JavaDomReference

/**
 * @author cdgeass
 * @since 2021-03-18
 */
class DomMethodConverter : Converter<PsiMethod>(), CustomReferenceConverter<PsiMethod> {

    override fun toString(t: PsiMethod?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiMethod? {
        if (s.isNullOrBlank() || context == null) return null

        val mapper = DomUtil.findDomElement(context.referenceXmlElement, Mapper::class.java) ?: return null
        val psiClass = mapper.getNamespace().value ?: return null

        psiClass.allMethods.forEach { psiMethod ->
            if (psiMethod.name == s) return psiMethod
        }
        return null
    }

    override fun createReferences(
        value: GenericDomValue<PsiMethod>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(JavaDomReference(element, value?.value?.let { listOf(it) }))
    }

}