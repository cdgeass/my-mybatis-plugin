package io.github.cdgeass.editor.dom.references;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;

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
            return includeTags.stream()
                    .filter(includeTag -> Objects.equals(sqlId, includeTag.getAttributeValue(StringConstants.REFID)))
                    .map(includeTag -> Objects.requireNonNull(includeTag.getAttribute(StringConstants.REFID)).getValueElement())
                    .map(xmlAttributeValue -> new XmlReference<>(element, Collections.singletonList(xmlAttributeValue)))
                    .toArray(XmlReference[]::new);
        }
    }

    public static class IncludeReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var includeTag = (XmlTag) PsiTreeUtil.findFirstParent(element, psiElement ->
                    psiElement instanceof XmlTag
                            && ((XmlTag) psiElement).getAttribute(StringConstants.REFID) != null);
            if (includeTag == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            var refid = includeTag.getAttributeValue(StringConstants.REFID);
            if (StringUtils.isNotBlank(refid)) {
                return PsiReference.EMPTY_ARRAY;
            }

            var sqlTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                    element.getProject(), StringConstants.SQL);
            return sqlTags.stream()
                    .map(sqlTag -> sqlTag.getAttribute(StringConstants.ID))
                    .filter(Objects::nonNull)
                    .map(XmlAttribute::getValueElement)
                    .filter(Objects::nonNull)
                    .map(xmlAttributeValue -> new XmlReference<>(element, Collections.singletonList(xmlAttributeValue)))
                    .toArray(XmlReference[]::new);
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
            return xmlFiles.stream()
                    .flatMap(xmlFile -> PsiTreeUtil.findChildrenOfType(xmlFile, XmlAttribute.class).stream())
                    .filter(xmlAttribute -> StringConstants.RESULT_MAP.equals(xmlAttribute.getName()))
                    .map(XmlAttribute::getValueElement)
                    .filter(Objects::nonNull)
                    .filter(xmlAttributeValue -> Objects.equals(resultMapId, xmlAttributeValue.getValue()))
                    .map(xmlAttributeValue -> new XmlReference<>(element, Collections.singletonList(xmlAttributeValue)))
                    .toArray(XmlReference[]::new);
        }
    }

    public static class ResultMapAttributeReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var text = element.getText();
            if (StringUtils.isNotBlank(text)) {
                return PsiReference.EMPTY_ARRAY;
            }

            var resultMapTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                    element.getProject(), StringConstants.RESULT_MAP);
            return resultMapTags.stream()
                    .map(resultMapTag -> resultMapTag.getAttribute(StringConstants.ID))
                    .filter(Objects::nonNull)
                    .map(XmlAttribute::getValueElement)
                    .filter(Objects::nonNull)
                    .map(xmlAttributeValue -> new XmlReference<>(element, Collections.singletonList(xmlAttributeValue)))
                    .toArray(XmlReference[]::new);
        }
    }
}
