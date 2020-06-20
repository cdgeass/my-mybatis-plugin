package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import io.github.cdgeass.editor.dom.convert.AliasReferenceConvert;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-20
 */
public interface SelectKey extends DomElement {

    @Convert(AliasReferenceConvert.class)
    GenericAttributeValue<PsiClass> getResultType();

    GenericAttributeValue<String> getStatementType();

    GenericAttributeValue<String> getKeyProperty();

    GenericAttributeValue<String> getKeyColumn();

    GenericAttributeValue<String> getOrder();

    GenericAttributeValue<String> getDatabaseId();

    @SubTagList("include")
    List<Include> getIncludes();

    @SubTagList("trim")
    List<Trim> getTrims();

    @SubTagList("where")
    List<Where> getWheres();

    @SubTagList("set")
    List<Set> getSets();

    @SubTagList("foreach")
    List<Foreach> getForeachs();

    @SubTagList("choose")
    List<Choose> getChooses();

    @SubTagList("if")
    List<If> getIfs();

    @SubTagList("bind")
    List<Bind> getBinds();
}
