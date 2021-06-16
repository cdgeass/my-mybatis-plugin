package io.github.cdgeass.codeInsight

import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class XmlReferenceTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/xmlReference"
    }

    fun testResultMap() {
        val reference = myFixture.getReferenceAtCaretPosition("TestResultMap.xml")
        assertNotNull(reference)
        val psiElement = reference!!.resolve()
        assertInstanceOf(psiElement, XmlAttributeValue::class.java)
        assertEquals("BaseResultMap1", (psiElement as XmlAttributeValue).value)
    }

    fun testResultMapUsages() {
        val usages = myFixture.testFindUsages("TestResultMap.xml")
        assertSize(1, usages)
    }

}