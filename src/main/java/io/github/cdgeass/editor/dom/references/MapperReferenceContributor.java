package io.github.cdgeass.editor.dom.references;

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intellij.patterns.XmlPatterns.*;

/**
 * @author cdgeass
 * @since 2020-08-13
 */
public class MapperReferenceContributor extends PsiReferenceContributor {

    private static final XmlAttributeValuePattern SQL_REFERENCE_PATTERN = xmlAttributeValue().withSuperParent(2, xmlTag().withName(StringConstants.SQL));
    private static final XmlAttributeValuePattern INCLUDE_REFERENCE_PATTERN = xmlAttributeValue().withSuperParent(2, xmlTag().withName(StringConstants.INCLUDE));
    private static final XmlAttributeValuePattern RESULT_MAP_REFERENCE_PATTERN = xmlAttributeValue().withSuperParent(2, xmlTag().withName(StringConstants.RESULT_MAP));
    private static final XmlAttributeValuePattern RESULT_MAP_ATTRIBUTE_REFERENCE_PATTERN = xmlAttributeValue().withParent(xmlAttribute().withName(StringConstants.RESULT_MAP));

    private static final SqlReferenceProvider SQL_REFERENCE_PROVIDER = new SqlReferenceProvider();
    private static final IncludeReferenceProvider INCLUDE_REFERENCE_PROVIDER = new IncludeReferenceProvider();
    private static final ResultMapReferenceProvider RESULT_MAP_REFERENCE_PROVIDER = new ResultMapReferenceProvider();
    private static final ResultMapAttributeReferenceProvider RESULT_MAP_ATTRIBUTE_REFERENCE_PROVIDER = new ResultMapAttributeReferenceProvider();

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(SQL_REFERENCE_PATTERN, SQL_REFERENCE_PROVIDER);
        registrar.registerReferenceProvider(INCLUDE_REFERENCE_PATTERN, INCLUDE_REFERENCE_PROVIDER);
        registrar.registerReferenceProvider(RESULT_MAP_REFERENCE_PATTERN, RESULT_MAP_REFERENCE_PROVIDER);
        registrar.registerReferenceProvider(RESULT_MAP_ATTRIBUTE_REFERENCE_PATTERN, RESULT_MAP_ATTRIBUTE_REFERENCE_PROVIDER);
    }

    public static class SqlReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var sqlTag = (XmlTag) PsiTreeUtil.findFirstParent(element, psiElement ->
                    psiElement instanceof XmlTag
                            && ((XmlTag) psiElement).getAttributeValue(StringConstants.ID) != null);
            if (sqlTag == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            var sqlId = sqlTag.getAttributeValue(StringConstants.ID);

            var includeTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                    element.getProject(), StringConstants.INCLUDE);
            var targets = includeTags.stream()
                    .filter(includeTag -> Objects.equals(sqlId, includeTag.getAttributeValue(StringConstants.REFID)))
                    .map(includeTag -> Objects.requireNonNull(includeTag.getAttribute(StringConstants.REFID)).getValueElement())
                    .collect(Collectors.toList());
            return new PsiReference[]{new XmlReference<>(element, targets)};
        }
    }

    public static class IncludeReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var text = StringUtils.removeEnd(
                    Optional.ofNullable(((XmlAttribute) element.getParent()).getValue()).orElse("").trim(),
                    CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);

            var sqlTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                    element.getProject(), StringConstants.SQL);
            List<XmlAttributeValue> targets = Lists.newArrayList();
            List<XmlAttributeValue> variants = Lists.newArrayList();
            for (var sqlTag : sqlTags) {
                var attribute = sqlTag.getAttribute(StringConstants.ID);
                if (attribute != null && attribute.getValue() != null) {
                    var attributeValueString = attribute.getValue().trim();
                    if (attributeValueString.contains(text)) {
                        variants.add(attribute.getValueElement());
                    }
                    if (StringUtils.equals(attributeValueString, text)) {
                        targets.add(attribute.getValueElement());
                    }
                }
            }
            return new PsiReference[]{new XmlReference<>(element, targets, variants, XmlAttributeValue::getValue)};
        }
    }

    public static class ResultMapReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var resultMapTag = (XmlTag) PsiTreeUtil.findFirstParent(element, psiElement ->
                    psiElement instanceof XmlTag
                            && ((XmlTag) psiElement).getAttribute(StringConstants.ID) != null);
            if (resultMapTag == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            var resultMapId = resultMapTag.getAttributeValue(StringConstants.ID);

            var xmlFiles = DomUtil.findByNamespace(DomUtil.getContainingFileNameSpace(element), element.getProject());
            var targets = xmlFiles.stream()
                    .flatMap(xmlFile -> PsiTreeUtil.findChildrenOfType(xmlFile, XmlAttribute.class).stream())
                    .filter(xmlAttribute -> StringConstants.RESULT_MAP.equals(xmlAttribute.getName()))
                    .map(XmlAttribute::getValueElement)
                    .filter(Objects::nonNull)
                    .filter(xmlAttributeValue -> Objects.equals(resultMapId, xmlAttributeValue.getValue()))
                    .collect(Collectors.toList());
            return new PsiReference[]{new XmlReference<>(element, targets)};
        }
    }

    public static class ResultMapAttributeReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var text = StringUtils.removeEnd(
                    Optional.ofNullable(((XmlAttribute) element.getParent()).getValue()).orElse("").trim(),
                    CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);

            var resultMapTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                    element.getProject(), StringConstants.RESULT_MAP);
            List<XmlAttributeValue> targets = Lists.newArrayList();
            List<XmlAttributeValue> variants = Lists.newArrayList();
            for (var resultMapTag : resultMapTags) {
                var attribute = resultMapTag.getAttribute(StringConstants.ID);
                if (attribute != null && attribute.getValue() != null) {
                    var attributeValueString = attribute.getValue().trim();
                    if (attributeValueString.contains(text)) {
                        variants.add(attribute.getValueElement());
                    }
                    if (StringUtils.equals(attributeValueString, text)) {
                        targets.add(attribute.getValueElement());
                    }
                }
            }
            return new PsiReference[]{new XmlReference<>(element, targets, variants, XmlAttributeValue::getValue)};
        }
    }
}
