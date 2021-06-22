package io.github.cdgeass.codeInsight

import com.intellij.pom.PomTargetPsiElement
import com.intellij.psi.PsiReference
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomTarget
import io.github.cdgeass.codeInsight.dom.element.ResultMap
import io.github.cdgeass.codeInsight.dom.element.Select

/**
 * @author cdgeass
 * @since 2021-06-22
 */
class ResolveAnnotationTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/resolveAnnotation"
    }

    private fun <T> getDomElement(reference: PsiReference, clazz: Class<T>): DomElement {
        val psiElement = reference.resolve()
        assertNotNull(psiElement)
        assertInstanceOf(psiElement, PomTargetPsiElement::class.java)

        val target = (psiElement as PomTargetPsiElement).target
        assertInstanceOf(target, DomTarget::class.java)
        val domElement = (target as DomTarget).domElement
        assertInstanceOf(domElement, clazz)

        return domElement
    }

    fun testResultMap() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("ResultMap.xml")

        val domElement = getDomElement(reference, ResultMap::class.java)
        assertEquals("BaseResultMap", (domElement as ResultMap).getId().value)
    }

    fun testArgSelect() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("ArgSelect.xml")

        val domElement = getDomElement(reference, Select::class.java)
        assertEquals("select", (domElement as Select).getId().rawText)
    }

    fun testArgResultMap() {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("ArgResultMap.xml")

        val domElement = getDomElement(reference, ResultMap::class.java)
        assertEquals("IdResultMap", (domElement as ResultMap).getId().value)
    }
//
//    fun testAssociationSelect() {
//        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("AssociationSelect.xml")
//
//        val domElement = getDomElement(reference, Select::class.java)
//        assertEquals("select", (domElement as Select).getId().rawText)
//    }
//
//    fun testAssociationResultMap() {
//        val reference = myFixture.getReferenceAtCaretPositionWithAssertion("AssociationResultMap.xml")
//
//        val domElement = getDomElement(reference, ResultMap::class.java)
//        assertEquals("IdResultMap", (domElement as ResultMap).getId().value)
//    }
}