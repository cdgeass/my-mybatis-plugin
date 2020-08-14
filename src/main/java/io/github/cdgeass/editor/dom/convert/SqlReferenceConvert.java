package io.github.cdgeass.editor.dom.convert;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Sql;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public class SqlReferenceConvert extends Converter<Sql> implements CustomReferenceConverter<Sql> {

    @Nullable
    @Override
    public Sql fromString(@Nullable String s, ConvertContext context) {
        if (s == null) {
            return null;
        }
        var file = context.getFile();
        var domManager = DomManager.getDomManager(context.getProject());
        var fileElement = domManager.getFileElement(file, Mapper.class);
        if (fileElement == null) {
            return null;
        }
        var mapper = fileElement.getRootElement();
        var sqls = mapper.getSqls();
        for (var sql : sqls) {
            var idAttributeValue = sql.getId();
            var id = idAttributeValue.getValue();
            if (StringUtils.equals(id, s)) {
                return sql;
            }
        }

        // find from other files with the same namespace
        var rootTag = file.getRootTag();
        if (rootTag == null) {
            return null;
        }
        var namespace = rootTag.getAttributeValue(StringConstants.NAMESPACE);
        var psiManager = PsiManager.getInstance(context.getProject());
        var virtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(context.getProject()));
        sqls = virtualFiles
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
                    return StringConstants.MAPPER.equals(xmlRootTag.getName())
                            && StringUtils.equals(namespace, xmlRootTag.getAttributeValue(StringConstants.NAMESPACE));
                })
                .map(xmlFile -> {
                    var xmlFileElement = domManager.getFileElement(xmlFile, Mapper.class);
                    if (xmlFileElement == null) {
                        return null;
                    }
                    return xmlFileElement.getRootElement();
                })
                .filter(Objects::nonNull)
                .flatMap(tempMapper -> tempMapper.getSqls().stream())
                .collect(Collectors.toList());
        for (var sql : sqls) {
            var idAttributeValue = sql.getId();
            var id = idAttributeValue.getValue();
            if (StringUtils.equals(id, s)) {
                return sql;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable Sql sql, ConvertContext context) {
        if (sql == null) {
            return null;
        }
        var idAttributeValue = sql.getId();
        if (idAttributeValue == null) {
            return null;
        }
        return idAttributeValue.getValue();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<Sql> value, PsiElement element, ConvertContext context) {
        var sql = value.getValue();
        if (sql == null || sql.getXmlTag() == null) {
            return new PsiReference[0];
        }

        return new PsiReference[]{new XmlReference(element, Collections.singletonList(sql.getXmlTag()))};
    }
}
