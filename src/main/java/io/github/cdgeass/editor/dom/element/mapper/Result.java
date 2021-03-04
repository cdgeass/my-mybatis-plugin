package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since 2020-06-19
 */
public interface Result extends DomElement {

    GenericAttributeValue<String> getProperty();

    GenericAttributeValue<String> getJavaType();

    GenericAttributeValue<String> getColumn();

    GenericAttributeValue<String> getJdbcType();

    GenericAttributeValue<String> typeHandler();
}
