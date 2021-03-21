package io.github.cdgeass.codeInsight.dom.description

import com.intellij.util.xml.DomFileDescription
import io.github.cdgeass.codeInsight.dom.element.Configuration

/**
 * @author cdgeass
 * @since 2021/3/21
 */
class ConfigurationDescription : DomFileDescription<Configuration>(Configuration::class.java, "configuration")