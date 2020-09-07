package io.github.cdgeass.editor.dom.references;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.intellij.patterns.XmlPatterns.*;

/**
 * @author cdgeass
 * @since 2020-08-13
 */
public class MapperReferenceContributor extends PsiReferenceContributor {

    private static final XmlAttributeValuePattern SQL_PATTERN = xmlAttributeValue().withParent(
            xmlAttribute().withName(StringConstants.ID).withParent(
                    xmlTag().withName(StringConstants.SQL)));
    private static final XmlAttributeValuePattern RESULT_MAP_PATTERN = xmlAttributeValue().withParent(
            xmlAttribute().withName(StringConstants.ID).withParent(
                    xmlTag().withName(StringConstants.RESULT_MAP)));

    private static final SqlReferenceProvider SQL_REFERENCE_PROVIDER = new SqlReferenceProvider();
    private static final ResultMapReferenceProvider RESULT_MAP_REFERENCE_PROVIDER = new ResultMapReferenceProvider();

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(SQL_PATTERN, SQL_REFERENCE_PROVIDER);
        registrar.registerReferenceProvider(RESULT_MAP_PATTERN, RESULT_MAP_REFERENCE_PROVIDER);
    }

    public static class SqlReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var sqlId = ((XmlAttributeValue) element).getValue();

            var includeTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                    element.getProject(), StringConstants.INCLUDE);
            var targets = includeTags.stream()
                    .map(includeTag -> includeTag.getAttribute(StringConstants.REFID))
                    .filter(xmlAttribute -> xmlAttribute != null && StringUtils.equals(sqlId, xmlAttribute.getValue()))
                    .collect(Collectors.toList());
            return new PsiReference[]{new XmlReference<>(element, targets)};
        }
    }

    public static class ResultMapReferenceProvider extends PsiReferenceProvider {

        @Override
        public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            var resultMapId = ((XmlAttributeValue) element).getValue();

            var xmlFiles = DomUtil.findByNamespace(DomUtil.getContainingFileNameSpace(element), element.getProject());
            var targets = xmlFiles.stream()
                    .flatMap(xmlFile -> PsiTreeUtil.findChildrenOfType(xmlFile, XmlAttribute.class).stream())
                    .filter(xmlAttribute -> StringConstants.RESULT_MAP.equals(xmlAttribute.getName())
                            && StringUtils.equals(resultMapId, xmlAttribute.getValue()))
                    .map(PsiElement::getNavigationElement)
                    .collect(Collectors.toList());
            return new PsiReference[]{new XmlReference<>(element, targets)};
        }
    }

}
