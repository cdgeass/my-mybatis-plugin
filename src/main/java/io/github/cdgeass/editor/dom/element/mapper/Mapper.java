package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import io.github.cdgeass.codeInsight.dom.element.Statement;

import java.util.List;

/**
 * @author cdgass
 * @since 2020-05-21
 */
public interface Mapper extends DomElement {

    GenericAttributeValue<PsiClass> getNamespace();

    @SubTag("cache-ref")
    CacheRef getCacheRef();

    @SubTagsList({"select", "delete", "update", "insert"})
    List<Statement> getStatements();

    @SubTag("cache")
    Cache getCache();

    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

    @SubTagList("parameterMap")
    List<ParameterMap> getParameterMap();

    @SubTagList("sql")
    List<Sql> getSqls();

    @SubTagList("select")
    List<SelectStatement> getSelects();

    @SubTagList("insert")
    List<ModificationStatement> getInserts();

    @SubTagList("update")
    List<ModificationStatement> getUpdates();

    @SubTagList("delete")
    List<ModificationStatement> getDeletes();
}
