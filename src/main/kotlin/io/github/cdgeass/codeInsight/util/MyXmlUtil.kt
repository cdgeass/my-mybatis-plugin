package io.github.cdgeass.codeInsight.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.indexing.FileBasedIndex
import io.github.cdgeass.codeInsight.MyXmlNamespaceIndex

/**
 * @author cdgeass
 * @since 2021-03-17
 */
/**
 * 根据命名空间查找xml mapper
 */
fun findByNamespace(namespace: String, project: Project): Collection<XmlFile> {
    val files = FileBasedIndex.getInstance()
        .getContainingFiles(MyXmlNamespaceIndex.NAME, namespace, GlobalSearchScope.allScope(project))

    val psiManager = PsiManager.getInstance(project)
    return files.mapNotNull { virtualFile ->
        val psiFile = psiManager.findFile(virtualFile)
        if (psiFile is XmlFile) {
            psiFile
        } else {
            null
        }
    }
}

/**
 * 判断 psiElement 是否是 mapper statement
 */
fun isStatementTag(element: PsiElement): Boolean {
    return element is XmlTag
            && (element.name == "select"
            || element.name == "delete"
            || element.name == "update"
            || element.name == "insert")
}