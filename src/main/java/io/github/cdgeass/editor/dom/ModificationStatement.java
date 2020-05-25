package io.github.cdgeass.editor.dom;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since  2020-05-22
 */
public interface ModificationStatement extends DomElement {

    @Convert(PsiJavaReferenceConvert.class)
    GenericAttributeValue<String> getId();
}
