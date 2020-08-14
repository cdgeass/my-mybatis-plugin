package io.github.cdgeass.editor.dom.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Sql;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-08-13
 */
public class SqlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttribute.class),
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        var sql = DomUtil.findDomElement(element, Sql.class);
                        if (sql == null || sql.getXmlTag() == null) {
                            return new PsiReference[0];
                        }

                        var idAttributeValue = sql.getId();
                        var sqlId = idAttributeValue.getValue();
                        if (sqlId == null) {
                            return new PsiReference[0];
                        }

                        var xmlTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(element),
                                element.getProject(), StringConstants.SQL);
                        var targets = xmlTags.stream()
                                .filter(xmlTag -> sqlId.equals(xmlTag.getAttributeValue("id")))
                                .map(PsiElement::getFirstChild)
                                .collect(Collectors.toList());
                        return new PsiReference[]{new XmlReference(element, targets)};
                    }
                });
    }
}
