package io.github.cdgeass.codeInsight.dom.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.xml.DomUtil
import io.github.cdgeass.editor.dom.element.mapper.Statement

/**
 * @author cdgeass
 * @since 2021/3/18
 */
class DomMethodReference(psiElement: PsiElement) : PsiReferenceBase<PsiElement>(psiElement) {

    override fun resolve(): PsiElement? {
        val statement = DomUtil.findDomElement(myElement, Statement::class.java) ?: return null
        return statement.id.value
    }


}