package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.*;
import io.github.cdgeass.editor.dom.element.convert.AliasReferenceConvert;
import io.github.cdgeass.editor.dom.element.convert.PsiMethodReferenceConvert;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-05-26
 */
public interface Statement extends DomElement {

    @Convert(PsiMethodReferenceConvert.class)
    GenericAttributeValue<PsiMethod> getId();

    @Attribute("parameterMap")
    GenericAttributeValue<ParameterMap> getParameterMap();

    @Convert(AliasReferenceConvert.class)
    GenericAttributeValue<PsiClass> getParameterType();

    GenericAttributeValue<String> getTimeout();

    GenericAttributeValue<Boolean> getFlushCache();

    GenericAttributeValue<String> getStatementType();

    GenericAttributeValue<String> getDatabaseId();

    GenericAttributeValue<String> getLang();

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
