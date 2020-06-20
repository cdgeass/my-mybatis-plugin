package io.github.cdgeass.editor.dom;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
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
        XmlFile file = context.getFile();
        var domManager = DomManager.getDomManager(context.getProject());
        var fileElement = domManager.getFileElement(file, Mapper.class);
        if (fileElement == null) {
            return null;
        }
        var mapper = fileElement.getRootElement();
        var resultMaps = mapper.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
            var idAttributeValue = resultMap.getId();
            var id = idAttributeValue.getValue();
            if (StringUtils.equals(id, s)) {
                return resultMap;
            }
        }

        // find from other files with the same namespace
        var rootTag = file.getRootTag();
        if (rootTag == null) {
            return null;
        }
        var namespace = rootTag.getAttributeValue("namespace");
        var psiManager = PsiManager.getInstance(context.getProject());
        var virtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(context.getProject()));
        resultMaps = virtualFiles
                .stream()
                .map(psiManager::findFile)
                .filter(Objects::nonNull)
                .map(virtualFile -> (XmlFile) virtualFile)
                .filter(xmlFile -> {
                    if (Objects.equals(file, xmlFile)) {
                        return false;
                    }
                    var xmlRootTag = xmlFile.getRootTag();
                    if (xmlRootTag == null) {
                        return false;
                    }
                    return StringUtils.equals(namespace, xmlRootTag.getAttributeValue("namespace"));
                })
                .map(xmlFile -> {
                    var xmlFileElement = domManager.getFileElement(xmlFile, Mapper.class);
                    if (xmlFileElement == null) {
                        return null;
                    }
                    return xmlFileElement.getRootElement();
                })
                .filter(Objects::nonNull)
                .flatMap(tempMapper -> tempMapper.getResultMaps().stream())
                .collect(Collectors.toList());
        for (ResultMap resultMap : resultMaps) {
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
            return new PsiReference[0];
        }

        return new PsiReference[]{new XmlReference(element, Collections.singletonList(resultMap.getXmlTag()))};
    }
}
