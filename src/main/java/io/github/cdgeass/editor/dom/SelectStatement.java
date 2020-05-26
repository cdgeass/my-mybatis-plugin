package io.github.cdgeass.editor.dom;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public interface SelectStatement extends Statement {

    @Attribute("resultMap")
    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getResultMap();
}
