package io.github.cdgeass.codeInsight.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomManager
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.dom.element.ResultMap
import io.github.cdgeass.codeInsight.dom.element.Sql
import io.github.cdgeass.codeInsight.dom.element.Statement

/**
 * @author cdgeass
 * @since 2021/3/20
 */
fun getMappers(namespace: String, project: Project): List<Mapper> {
    val xmlFiles = findByNamespace(namespace, project)
    val domManager = DomManager.getDomManager(project)
    return xmlFiles.mapNotNull { domManager.getFileElement(it, Mapper::class.java)?.rootElement }
}

fun getMappers(psiElement: PsiElement): List<Mapper> {
    val project = psiElement.project

    val file = psiElement.containingFile
    if (file !is XmlFile) return emptyList()

    val namespace = file.rootTag?.getAttributeValue("namespace")
    if (namespace.isNullOrBlank()) return emptyList()

    return getMappers(namespace, project)
}

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
