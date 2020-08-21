package io.github.cdgeass.editor.dom.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Include;
import io.github.cdgeass.editor.dom.element.mapper.ResultMap;
import io.github.cdgeass.editor.dom.element.mapper.Sql;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-08-13
 */
public class ReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        var sql = DomUtil.findDomElement(element, Sql.class);
                        if (sql == null || sql.getXmlTag() == null) {
                            return new PsiReference[0];
                        }

                        var idAttributeValue = sql.getId();
                        var id = idAttributeValue.getValue();
                        if (id == null) {
                            return new PsiReference[0];
                        }

                        var includeTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                                element.getProject(), StringConstants.INCLUDE);
                        var targets = includeTags.stream()
                                .filter(includeTag -> id.equals(includeTag.getAttributeValue(StringConstants.REFID)))
                                .map(PsiElement::getNavigationElement)
                                .collect(Collectors.toList());
                        return new PsiReference[]{new XmlReference(element, targets)};
                    }
                });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        var include = DomUtil.findDomElement(element, Include.class);
                        if (include == null || include.getXmlTag() == null) {
                            return new PsiReference[0];
                        }

                        var refid = include.getXmlTag().getAttributeValue(StringConstants.REFID);
                        if (StringUtils.isNotBlank(refid)) {
                            return new PsiReference[0];
                        }

                        var sqlTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                                element.getProject(), StringConstants.SQL);
                        var targets = sqlTags.stream()
                                .map(sqlTag -> sqlTag.getAttribute(StringConstants.ID))
                                .filter(Objects::nonNull)
                                .map(PsiElement::getNavigationElement)
                                .collect(Collectors.toList());
                        return new PsiReference[]{new XmlReference(element, targets, psiElement -> {
                            if (psiElement instanceof XmlAttribute) {
                                return ((XmlAttribute) psiElement).getValue();
                            }
                            return psiElement.getText();
                        })};
                    }
                });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        var resultMap = DomUtil.findDomElement(element, ResultMap.class);
                        if (resultMap == null || resultMap.getXmlTag() == null) {
                            return new PsiReference[0];
                        }

                        var id = resultMap.getId().getValue();
                        if (StringUtils.isBlank(id)) {
                            return new PsiReference[0];
                        }

                        var xmlFiles = DomUtil.findByNamespace(DomUtil.getContainingFileNameSpace(element),
                                element.getProject());
                        var resultMapAttributes = xmlFiles.stream()
                                .flatMap(xmlFile -> PsiTreeUtil.findChildrenOfType(xmlFile, XmlAttribute.class).stream())
                                .filter(xmlAttribute -> Objects.equals(StringConstants.RESULT_MAP, xmlAttribute.getName())
                                        && StringUtils.isNotBlank(xmlAttribute.getValue()))
                                .collect(Collectors.toList());
                        var targets = resultMapAttributes.stream()
                                .map(PsiElement::getNavigationElement)
                                .collect(Collectors.toList());
                        return new PsiReference[]{new XmlReference(element, targets)};
                    }
                });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        var xmlAttribute = element.getParent();
                        if (!(xmlAttribute instanceof XmlAttribute)
                                || !Objects.equals(StringConstants.RESULT_MAP, ((XmlAttribute) xmlAttribute).getName())
                                || StringUtils.isNotBlank(((XmlAttributeValue) element).getValue())) {
                            return new PsiReference[0];
                        }

                        var resultMapTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                                element.getProject(), StringConstants.RESULT_MAP);
                        var targets = resultMapTags.stream()
                                .map(resultMapTag -> resultMapTag.getAttribute(StringConstants.ID))
                                .filter(Objects::nonNull)
                                .map(PsiElement::getNavigationElement)
                                .collect(Collectors.toList());
                        return new PsiReference[]{new XmlReference(element, targets, psiElement -> {
                            if (psiElement instanceof XmlAttribute) {
                                return ((XmlAttribute) psiElement).getValue();
                            }
                            return psiElement.getText();
                        })};
                    }
                });
    }
}
