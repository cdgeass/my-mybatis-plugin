package io.github.cdgeass.editor.dom.references;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-08-27
 */
public class MapperCompletionContributor extends CompletionContributor {

    private static final ElementPattern<XmlToken> INCLUDE_PATTERN = XmlPatterns.instanceOf(XmlToken.class);
    private static final ElementPattern<XmlToken> RESULT_MAP_PATTERN = XmlPatterns.instanceOf(XmlToken.class);

    private static final CompletionProvider<CompletionParameters> INCLUDE_COMPLETION_PROVIDER = new IncludeCompletionProvider();
    private static final CompletionProvider<CompletionParameters> RESULT_MAP_COMPLETION_PROVIDER = new ResultMapCompletionProvider();

    public MapperCompletionContributor() {
        extend(CompletionType.BASIC, INCLUDE_PATTERN, INCLUDE_COMPLETION_PROVIDER);
        extend(CompletionType.BASIC, RESULT_MAP_PATTERN, RESULT_MAP_COMPLETION_PROVIDER);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }

    public static class IncludeCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            var position = parameters.getPosition();
            var parent = PsiTreeUtil.findFirstParent(position, psiElement -> psiElement instanceof XmlAttribute
                    && StringUtils.equals(StringConstants.REFID, ((XmlAttribute) psiElement).getName()));
            if (parent == null) {
                return;
            }

            var text = StringUtils.removeEnd(position.getText(), CompletionUtilCore.DUMMY_IDENTIFIER);

            var sqlTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(position),
                    position.getProject(), StringConstants.SQL);
            var lookupElements = sqlTags.stream()
                    .map(sqlTag -> sqlTag.getAttribute(StringConstants.ID))
                    .filter(xmlAttribute -> xmlAttribute != null && StringUtils.contains(xmlAttribute.getValue(), text))
                    .map(xmlAttribute -> LookupElementBuilder.createWithSmartPointer(xmlAttribute.getValue(), xmlAttribute)
                            .withIcon(AllIcons.FileTypes.Xml))
                    .collect(Collectors.toList());
            result.addAllElements(lookupElements);
        }
    }

    public static class ResultMapCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            var position = parameters.getPosition();
            var parent = PsiTreeUtil.findFirstParent(position, psiElement -> psiElement instanceof XmlAttribute
                    && StringUtils.equals(StringConstants.RESULT_MAP, ((XmlAttribute) psiElement).getName()));
            if (parent == null) {
                return;
            }

            var text = StringUtils.removeEnd(position.getText(), CompletionUtilCore.DUMMY_IDENTIFIER);

            var resultMapTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(position),
                    position.getProject(), StringConstants.RESULT_MAP);
            var lookupElements = resultMapTags.stream()
                    .map(sqlTag -> sqlTag.getAttribute(StringConstants.ID))
                    .filter(xmlAttribute -> xmlAttribute != null && StringUtils.contains(xmlAttribute.getValue(), text))
                    .map(xmlAttribute -> LookupElementBuilder.createWithSmartPointer(xmlAttribute.getValue(), xmlAttribute)
                            .withIcon(AllIcons.FileTypes.Xml))
                    .collect(Collectors.toList());
            result.addAllElements(lookupElements);
        }
    }
}
