package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-20
 */
public interface Sql extends DomElement {

    GenericAttributeValue<String> getId();

    GenericAttributeValue<String> getLang();

    GenericAttributeValue<String> getDatabaseid();

    @SubTagList("include")
    List<Include> getIncludes();

    @SubTagList("trim")
    List<Trim> getTrims();

    @SubTagList("where")
    List<Where> getWhere();

    @SubTagList("set")
    List<Set> getSets();

    @SubTagList("foreach")
    List<Foreach> getForeachs();

    @SubTagList("choose")
    List<Choose> getChooses();

    @SubTagList("if")
    List<If> getIfs();

    @SubTagList("bind")
    List<Bind> getBind();
}
