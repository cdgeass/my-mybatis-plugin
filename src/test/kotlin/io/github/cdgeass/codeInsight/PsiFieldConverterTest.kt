package io.github.cdgeass.codeInsight

import com.intellij.psi.PsiField
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.cdgeass.codeInsight.reference.MyPsiFieldReference

/**
 * @author cdgeass
 * @since 2021-07-02
 */
class PsiFieldConverterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/psiFieldConverter"
    }

    fun testReference() {
        val reference =
            myFixture.getReferenceAtCaretPositionWithAssertion("PsiFieldConverter.xml", "PsiFieldConverter.java")
        assertInstanceOf(reference, MyPsiFieldReference::class.java)
        val psiElement = reference.resolve()
        assertInstanceOf(psiElement, PsiField::class.java)
        assertEquals("id", (psiElement as PsiField).name)
    }

    fun testUsages() {
        val usages = myFixture.testFindUsages("PsiFieldConverterUsages.java", "PsiFieldConverterUsages.xml")
        assertSize(1, usages)
    }
}
