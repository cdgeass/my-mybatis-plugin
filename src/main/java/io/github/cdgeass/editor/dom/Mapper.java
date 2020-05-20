package io.github.cdgeass.editor.dom;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgass
 * @since 2020-05-21
 */
public interface Mapper extends DomElement {

    @Attribute("namespace")
    GenericAttributeValue<String> getNamespace();
}
