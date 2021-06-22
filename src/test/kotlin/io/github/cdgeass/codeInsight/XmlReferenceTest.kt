package io.github.cdgeass.codeInsight

import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * @author cdgeass
 * @since 2021-06-16
 */
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

    fun testResultMapNoNamespace() {
        val reference = myFixture.getReferenceAtCaretPosition("TestResultMapNoNamespace.xml")
        assertNull(reference?.resolve())
    }

    fun testResultMapUsages() {
        val usages = myFixture.testFindUsages("TestResultMap.xml")
        assertSize(1, usages)
    }

    fun testResultMapUsagesNoNamespace() {
        assertThrows(AssertionError::class.java) {
            myFixture.testFindUsages("TestResultMapNoNamespace.xml")
        }
    }

    fun testSql() {
        val reference = myFixture.getReferenceAtCaretPosition("TestSql.xml")
        assertNotNull(reference)
        val psiElement = reference!!.resolve()
        assertInstanceOf(psiElement, XmlAttributeValue::class.java)
        assertEquals("sql1", (psiElement as XmlAttributeValue).value)
    }

    fun testSqlUsages() {
        val usages = myFixture.testFindUsages("TestSql.xml")
        assertSize(1, usages)
    }
}
