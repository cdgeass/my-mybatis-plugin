package io.github.cdgeass.codeInsight.dom.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomUtil
import io.github.cdgeass.codeInsight.util.getNavigationElement

/**
 * @author cdgeass
 * @since 2021/3/18
 */
class JavaDomReference(psiElement: PsiElement) : PsiReferenceBase<PsiElement>(psiElement) {

    override fun resolve(): PsiElement? {
        val domElement = DomUtil.findDomElement(myElement, DomElement::class.java) ?: return null
        return getNavigationElement(domElement)
    }

}