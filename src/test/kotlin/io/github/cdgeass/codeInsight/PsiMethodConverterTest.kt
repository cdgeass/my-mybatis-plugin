package io.github.cdgeass.codeInsight

import com.intellij.psi.PsiMethod
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.cdgeass.codeInsight.reference.MyPsiMethodReference

/**
 * @author cdgeass
 * @since 2021-07-05
 */
class PsiMethodConverterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/psiMethodConverter"
    }

    fun testReference() {
        val reference =
            myFixture.getReferenceAtCaretPositionWithAssertion("PsiMethodConverter.xml", "PsiMethodConverter.java")
        assertInstanceOf(reference, MyPsiMethodReference::class.java)
        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, PsiMethod::class.java)
        assertEquals("find", (psiElement as PsiMethod).name)
    }

    fun testUsages() {
        val usages = myFixture.testFindUsages("PsiMethodConverterUsages.java", "PsiMethodConverterUsages.xml")
        assertSize(1, usages)
    }
}