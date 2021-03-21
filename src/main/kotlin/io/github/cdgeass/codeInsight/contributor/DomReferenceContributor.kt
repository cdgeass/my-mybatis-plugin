package io.github.cdgeass.codeInsight.contributor

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.search.UsageSearchContext
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import io.github.cdgeass.codeInsight.reference.MyPsiElementReference

/**
 * @author cdgeass
 * @since 2021/3/21
 */
private val INVALID_ATTRIBUTES = listOf("type", "resultType")

class DomReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlAttributeValue::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<PsiReference> {
                    val psiSearchHelper = PsiSearchHelper.getInstance(element.project)
                    val psiElements = mutableListOf<PsiElement>()
                    psiSearchHelper.processElementsWithWord(
                        { patternedElement, _ ->
                            if (isValid(element as XmlAttributeValue, patternedElement)) {
                                psiElements.add(patternedElement)
                            }
                            true
                        },
                        GlobalSearchScope.allScope(element.project),
                        element.text,
                        UsageSearchContext.IN_PLAIN_TEXT,
                        false
                    )
                    if (psiElements.isEmpty()) {
                        return PsiReference.EMPTY_ARRAY
                    }
                    return arrayOf(MyPsiElementReference(element, psiElements))
                }
            }
        )
    }

    private fun isValid(element: XmlAttributeValue, patternedElement: PsiElement): Boolean {
        if (isInValidAttribute(element)) {
            return false
        }

        if (patternedElement !is XmlAttributeValue) {
            return false
        }

        val xmlTag = getTag(element)
        val patternedXmlTag = getTag(patternedElement)
        if (xmlTag == null || patternedXmlTag == null || xmlTag.name == patternedXmlTag.name) {
            return false
        }

        return element.value == patternedElement.value
    }

    private fun isInValidAttribute(element: XmlAttributeValue): Boolean {
        val attribute = getAttribute(element) ?: return false

        val name = attribute.name
        return INVALID_ATTRIBUTES.contains(name)
    }

    private fun getAttribute(element: XmlAttributeValue): XmlAttribute? {
        val parent = PsiTreeUtil.findFirstParent(element) { it is XmlAttribute } ?: return null
        return parent as XmlAttribute
    }

    private fun getTag(element: PsiElement): XmlTag? {
        val parent = PsiTreeUtil.findFirstParent(element) { it is XmlTag } ?: return null
        return parent as XmlTag
    }
}