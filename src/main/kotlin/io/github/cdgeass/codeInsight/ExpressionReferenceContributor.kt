package io.github.cdgeass.codeInsight

import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.xml.XmlTokenType

/**
 * @author cdgeass
 * @since 2021/3/28
 */

class ExpressionReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val expressionReferenceProvider = ExpressionReferenceProvider()
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("test")
                    .withParent(XmlPatterns.xmlTag().withName("if"))
            ),
            expressionReferenceProvider
        )
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("collection")
                    .withParent(XmlPatterns.xmlTag().withName("foreach"))
            ),
            expressionReferenceProvider
        )
        registrar.registerReferenceProvider(
            XmlPatterns.psiElement().withElementType(XmlTokenType.XML_DATA_CHARACTERS),
            expressionReferenceProvider
        )
    }

}
