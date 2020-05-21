package io.github.cdgeass.editor.dom;

import com.intellij.util.xml.*;

/**
 * @author cdgeass
 * @since  2020-05-21
 */
public interface Select extends DomElement {

    @Convert(ReferenceConvert.class)
    GenericAttributeValue<String> getId();
}
