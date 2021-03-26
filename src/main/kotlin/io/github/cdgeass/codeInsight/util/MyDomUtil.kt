package io.github.cdgeass.codeInsight.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomManager
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.dom.element.Statement
import io.github.cdgeass.codeInsight.dom.element.WithIdDomElement

/**
 * @author cdgeass
 * @since 2021/3/20
 */
fun getMappers(namespace: String, project: Project): List<Mapper> {
    val xmlFiles = findByNamespace(namespace, project)
    val domManager = DomManager.getDomManager(project)
    return xmlFiles.mapNotNull { domManager.getFileElement(it, Mapper::class.java)?.rootElement }
}

fun getMappers(element: PsiElement): List<Mapper> {
    val project = element.project

    val file = element.containingFile
    if (file !is XmlFile) return emptyList()

    val namespace = file.rootTag?.getAttributeValue("namespace")
    if (namespace.isNullOrBlank()) return emptyList()

    return getMappers(namespace, project)
}

fun resolveElementReferences(element: PsiElement, ignored: Boolean = false): List<DomElement> {
    if (element !is XmlAttributeValue) return emptyList()

    val attributeName = PsiTreeUtil.findFirstParent(element) {
        it is XmlAttribute
    }?.let { it as XmlAttribute }?.name ?: return emptyList()

    var attributeValue = element.value
    val mappers = if (attributeValue.contains(".")) {
        val xmlTag = element.parentOfType<XmlTag>()
        val namespace = if (xmlTag?.name == "cache-ref") {
            // cache-ref 直接应用 namespace
            attributeValue
        } else {
            val temp = attributeValue.substringBeforeLast(".")
            attributeValue = attributeValue.substringAfterLast(".")
            temp
        }

        getMappers(namespace, element.project)
    } else {
        getMappers(element)
    }
    return when (attributeName) {
        "resultMap", "extends" -> {
            mappers.flatMap { it.getResultMaps() }
                .filter {
                    ignored || it.getId().value == attributeValue
                }
        }
        "refid" -> {
            mappers.flatMap { it.getSqls() }
                .filter {
                    ignored || it.getId().value == attributeValue
                }
        }
        "select" -> {
            mappers.flatMap { it.getSelects() }
                .filter {
                    ignored || it.getId().rawText == attributeValue
                }
        }
        "namespace" -> {
            mappers.flatMap { it.getCaches() }
        }
        else -> {
            emptyList()
        }
    }
}

fun getPsiElement(domElement: DomElement): PsiElement? {
    return when (domElement) {
        is WithIdDomElement -> {
            domElement.getId().xmlAttributeValue
        }
        is Statement -> {
            domElement.getId().xmlAttributeValue
        }
        else -> {
            domElement.xmlElement
        }
    }
}