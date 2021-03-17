package io.github.cdgeass.codeInsight.util

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

/**
 * @author cdgeass
 * @since 2021-03-17
 */
class MyXmlNamespaceIndex : ScalarIndexExtension<String>() {

    companion object {
        val NAME: ID<String, Void> = ID.create("io.github.cdgeass.codeInsight.util.MyXmlNamespaceIndex")
    }

    override fun getName(): ID<String, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer<String, Void, FileContent> {
            val namespace = getNamespace(it)
            if (namespace != null) {
                mapOf(Pair(namespace, null))
            } else {
                emptyMap<String, Void>()
            }
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getVersion(): Int {
        return 0
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return DefaultFileTypeSpecificInputFilter(XmlFileType.INSTANCE)
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    private fun getNamespace(fileContent: FileContent): String? {
        val psiFile = fileContent.psiFile
        val xmlTags = PsiTreeUtil.findChildrenOfType(psiFile, XmlTag::class.java)
        xmlTags.forEach { xmlTag ->
            if (xmlTag.name == "mapper" && xmlTag.getAttributeValue("namespace") != null) {
                return xmlTag.getAttributeValue("namespace")
            }
        }

        return null
    }
}