package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-20
 */
public interface Cache extends DomElement {

    GenericAttributeValue<String> getType();

    GenericAttributeValue<String> getEviction();

    GenericAttributeValue<String> getFlushInterval();

    GenericAttributeValue<String> getSize();

    GenericAttributeValue<String> getReadOnly();

    GenericAttributeValue<String> getBlocking();

    @SubTagList("property")
    List<Property> getProperties();
}
