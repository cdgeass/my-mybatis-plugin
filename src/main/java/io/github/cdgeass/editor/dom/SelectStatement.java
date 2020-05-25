package io.github.cdgeass.editor.dom;

import com.intellij.util.xml.*;

/**
 * @author cdgeass
 * @since  2020-05-21
 */
public interface SelectStatement extends DomElement {

    @Convert(PsiJavaReferenceConvert.class)
    GenericAttributeValue<String> getId();

    ResultMap getResultMap();
}
