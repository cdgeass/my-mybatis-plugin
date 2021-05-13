package io.github.cdgeass.codeInsight.dom.description

import com.intellij.util.xml.DomFileDescription
import io.github.cdgeass.codeInsight.dom.element.Mapper

/**
 * @author cdgeass
 * @since 2021-03-18
 */
class MapperDescription : DomFileDescription<Mapper>(Mapper::class.java, "mapper")
