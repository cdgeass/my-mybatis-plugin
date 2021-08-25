package io.github.cdgeass.codeInsight.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import com.intellij.util.indexing.FileBasedIndex
import io.github.cdgeass.codeInsight.MyXmlNamespaceIndex

/**
 * @author cdgeass
 * @since 2021-03-17
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
