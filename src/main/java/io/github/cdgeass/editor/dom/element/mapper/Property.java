package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since 2020-06-20
 */
public interface Property extends DomElement {

    GenericAttributeValue<String> getName();

    GenericAttributeValue<String> getValue();
}
