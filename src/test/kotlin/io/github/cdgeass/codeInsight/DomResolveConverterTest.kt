package io.github.cdgeass.codeInsight

import com.intellij.psi.xml.XmlTag
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * @author cdgeass
 * @since 2021-06-22
 */
class DomResolveConverterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/domResolveConverter"
    }

    fun testReference() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("DomResolveConverter.xml")
        assertNotNull(reference)

        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, XmlTag::class.java)
        assertEquals("BaseResultMap", (psiElement as XmlTag).getAttributeValue("id"))
    }

    fun testReferenceWithNoReference() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("DomResolveConverterWithNoReference.xml")
        assertNotNull(reference)

        val psiElement = reference.resolve();
        assertNull(psiElement)
    }

    fun testReferenceWithNamespace() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("DomResolveConverterWithNamespace2.xml", "DomResolveConverterWithNamespace1.xml")
        assertNotNull(reference)

        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, XmlTag::class.java)
        assertEquals("BaseResultMap", (psiElement as XmlTag).getAttributeValue("id"))
    }
}
