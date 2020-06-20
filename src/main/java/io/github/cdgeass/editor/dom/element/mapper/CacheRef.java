package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public interface CacheRef extends DomElement {

    GenericAttributeValue<String> getNamespace();
}
