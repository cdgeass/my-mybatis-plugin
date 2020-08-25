package io.github.cdgeass.editor.dom.element.convert;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.ResultMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-05-25
 */
public class ResultMapReferenceConvert extends Converter<ResultMap> implements CustomReferenceConverter<ResultMap> {

    @Nullable
    @Override
    public ResultMap fromString(@Nullable String s, ConvertContext context) {
        if (s == null) {
            return null;
        }

        var mappers = DomUtil.findByNamespace(DomUtil.getContainingFileNameSpace(context.getFile()),
                context.getProject(), Mapper.class);
        var resultMaps = mappers.stream()
                .map(Mapper::getResultMaps)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        for (var resultMap : resultMaps) {
            var idAttributeValue = resultMap.getId();
            var id = idAttributeValue.getValue();
            if (StringUtils.equals(id, s)) {
                return resultMap;
            }
        }

        return null;
    }

    @Override
    public @Nullable String toString(@Nullable ResultMap resultMap, ConvertContext context) {
        if (resultMap == null) {
            return null;
        }
        var idAttributeValue = resultMap.getId();
        if (idAttributeValue == null) {
            return null;
        }
        return idAttributeValue.getValue();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<ResultMap> value, PsiElement element, ConvertContext context) {
        var resultMap = value.getValue();
        if (resultMap == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return new PsiReference[]{new XmlReference<>(element, Collections.singletonList(resultMap.getXmlTag()))};
    }
}
