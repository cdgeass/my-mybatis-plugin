package io.github.cdgeass.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.xml.XmlFile
import com.intellij.sql.psi.SqlFile
import com.intellij.util.xml.DomManager
import io.github.cdgeass.codeInsight.dom.element.Mapper

/**
 * @author  cdgeass
 * @since   2022-03-11
 */
class MapperContext : TemplateContextType("MAPPER", "Mapper") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        if (file is SqlFile) {
            return file.name.endsWith(".xml")
        } else if (file !is XmlFile) {
            return false
        }

        val domManager = DomManager.getDomManager(file.project)
        return domManager.getFileElement(file, Mapper::class.java) != null
    }
}