package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.reference.JavaDomReference

/**
 * @author cdgeass
 * @since 2021/3/19
 */
class DomClassConverter : Converter<PsiClass>(), CustomReferenceConverter<PsiClass> {

    override fun toString(t: PsiClass?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiClass? {
        if (s.isNullOrBlank() || context == null) {
            return null
        }

        val element = context.invocationElement
        val scope: GlobalSearchScope? = if (element is GenericDomValue<*>) context.searchScope else null
        val psiClass = DomJavaUtil.findClass(s.trim(), context.file, context.module, scope)
        if (psiClass != null) {
            return psiClass
        }

        TODO("Configuration Alias")
    }

    override fun createReferences(
        value: GenericDomValue<PsiClass>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(JavaDomReference(element))
    }
}