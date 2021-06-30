package io.github.cdgeass.codeInsight

import com.intellij.psi.PsiClass
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.cdgeass.codeInsight.reference.MyJavaClassReference

class JavaClassConverterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/javaClassConverter"
    }

    fun testReference() {
        val reference =
            myFixture.getReferenceAtCaretPositionWithAssertion("JavaClassConverter.xml", "JavaClassConverter.java")
        assertInstanceOf(reference, MyJavaClassReference::class.java)
        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, PsiClass::class.java)
        assertEquals("Test", (psiElement as PsiClass).name)
    }

    fun testReferenceWithAlias() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion(
            "JavaClassConverterWithAlias.xml",
            "Configuration.xml",
            "JavaClassConverter.java"
        )
        assertInstanceOf(reference, MyJavaClassReference::class.java)
        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, PsiClass::class.java)
        assertEquals("Test", (psiElement as PsiClass).name)
    }

    fun testUsages() {
        val usages = myFixture.testFindUsages("JavaClassConverterUsages.java", "JavaClassConverterUsages.xml")
        assertNotEmpty(usages)
    }

}