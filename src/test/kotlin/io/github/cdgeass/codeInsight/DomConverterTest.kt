package io.github.cdgeass.codeInsight

import com.intellij.psi.xml.XmlTag
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * @author cdgeass
 * @since 2021-06-22
 */
class DomConverterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/domConverter"
    }

    fun testReference() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("DomConverter.xml")
        assertNotNull(reference)

        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, XmlTag::class.java)
        assertEquals("BaseResultMap", (psiElement as XmlTag).getAttributeValue("id"))
    }

    fun testReferenceWithNamespace() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("DomConverterWithNamespace2.xml", "DomConverterWithNamespace1.xml")
        assertNotNull(reference)

        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, XmlTag::class.java)
        assertEquals("BaseResultMap", (psiElement as XmlTag).getAttributeValue("id"))
    }
}
