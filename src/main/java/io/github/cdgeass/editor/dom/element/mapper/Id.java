package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since  2020-06-19
 */
public interface Id extends DomElement {

    GenericAttributeValue<String> getProperty();

    GenericAttributeValue<PsiClass> getJavaType();

    GenericAttributeValue<String> getColumn();

    GenericAttributeValue<String> getJdbcType();

    GenericAttributeValue<String> getTypeHandler();
}
