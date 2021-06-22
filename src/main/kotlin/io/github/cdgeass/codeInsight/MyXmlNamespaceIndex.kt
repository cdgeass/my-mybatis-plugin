package io.github.cdgeass.codeInsight

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

/**
 * @author cdgeass
 * @since 2021-03-17
 */
class MyXmlNamespaceIndex : ScalarIndexExtension<String>() {

    companion object {
        val NAME: ID<String, Void> = ID.create("io.github.cdgeass.codeInsight.MyXmlNamespaceIndex")
    }

    override fun getName(): ID<String, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer<String, Void, FileContent> {
            val namespace = getNamespace(it)
            if (namespace?.isNotBlank() == true) {
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
            } else if (xmlTag.name == "configuration") {
                // mybatis 配置文件
                return "mybatis.configuration"
            }
        }

        return null
    }
}
