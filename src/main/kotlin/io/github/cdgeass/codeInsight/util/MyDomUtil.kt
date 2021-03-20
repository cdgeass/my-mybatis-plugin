package io.github.cdgeass.codeInsight.util

import com.intellij.psi.PsiElement
import com.intellij.util.xml.DomElement
import io.github.cdgeass.codeInsight.dom.element.ResultMap
import io.github.cdgeass.codeInsight.dom.element.Statement

/**
 * @author cdgeass
 * @since 2021/3/20
 */
fun getNavigationElement(domElement: DomElement): PsiElement? {
    return when (domElement) {
        is Statement -> {
            getStatementNavigationElement(domElement)
        }
        is ResultMap -> {
            getResultMapNavigationElement(domElement)
        }
        else -> {
            null
        }
    }
}

private fun getStatementNavigationElement(statement: Statement): PsiElement? {
    return statement.getId().value
}

private fun getResultMapNavigationElement(resultMap: ResultMap): PsiElement? {
    return resultMap.getType().value
}

fun getIdentifyElement(domElement: DomElement): PsiElement? {
    return when (domElement) {
        is Statement -> {
            getStatementIdentifyElement(domElement)
        }
        is ResultMap -> {
            getResultMapIdentifyElement(domElement)
        }
        else -> {
            null
        }
    }
}

private fun getStatementIdentifyElement(statement: Statement): PsiElement? {
    return statement.getId().xmlAttributeValue
}

private fun getResultMapIdentifyElement(resultMap: ResultMap): PsiElement? {
    return resultMap.getId().xmlAttributeValue
}