package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;

import java.util.List;

/**
 * @author cdgass
 * @since 2020-05-21
 */
public interface Mapper extends DomElement {

    GenericAttributeValue<PsiClass> getNamespace();

    @SubTag("cache-ref")
    CacheRef getCacheRef();

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
