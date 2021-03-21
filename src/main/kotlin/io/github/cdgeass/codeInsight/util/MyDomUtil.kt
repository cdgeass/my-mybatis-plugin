package io.github.cdgeass.codeInsight.util

import com.intellij.psi.PsiElement
import com.intellij.util.xml.DomElement
import io.github.cdgeass.codeInsight.dom.element.ResultMap
import io.github.cdgeass.codeInsight.dom.element.Sql
import io.github.cdgeass.codeInsight.dom.element.Statement

/**
 * @author cdgeass
 * @since 2021/3/20
 */
fun getIdentifyElement(domElement: DomElement): PsiElement? {
    return when (domElement) {
        is Statement -> {
            getStatementIdentifyElement(domElement)
        }
        is ResultMap -> {
            getResultMapIdentifyElement(domElement)
        }
        is Sql -> {
            getSqlIdentifyElement(domElement)
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

private fun getSqlIdentifyElement(sql: Sql): PsiElement? {
    return sql.getId().xmlAttributeValue
}
