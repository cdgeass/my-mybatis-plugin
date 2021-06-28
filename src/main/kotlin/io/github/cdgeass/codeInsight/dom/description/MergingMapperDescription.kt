package io.github.cdgeass.codeInsight.dom.description

import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomFileElement
import com.intellij.util.xml.MergingFileDescription
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.util.findByNamespace

/**
 * @author cdgeass
 * @since 2021-06-23
 */
class MergingMapperDescription : MergingFileDescription<Mapper>(Mapper::class.java, "mapper") {

    override fun getFilesToMerge(element: DomElement): MutableSet<XmlFile> {
        val domFileElement = (element as DomFileElement<*>)

        val project = domFileElement.manager.project
        val namespace = domFileElement.rootTag?.getAttributeValue("namespace") ?: return HashSet()

        return HashSet(findByNamespace(namespace, project))
    }
}
