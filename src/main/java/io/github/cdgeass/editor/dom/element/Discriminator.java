package io.github.cdgeass.editor.dom.element;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-06-19
 */
public interface Discriminator extends DomElement {

    GenericAttributeValue<String> getColumn();

    GenericAttributeValue<String> getJavaType();

    GenericAttributeValue<String> getJdbcType();

    GenericAttributeValue<String> getTypeHandler();

    @SubTagList("case")
    List<Case> getCases();
}
