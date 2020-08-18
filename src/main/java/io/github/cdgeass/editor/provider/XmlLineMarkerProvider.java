package io.github.cdgeass.editor.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author cdgeass
 * @since 2020-05-17
 */
public class XmlLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof XmlFile)) {
            return;
        }

        var mapper = DomUtil.findDomElement((XmlFile) element, Mapper.class);
        if (mapper == null || mapper.getXmlTag() == null) {
            return;
        }
        var namespaceAttributeValue = mapper.getNamespace();
        var psiClass = namespaceAttributeValue.getValue();
        if (psiClass == null || psiClass.getQualifiedName() == null) {
            return;
        }
        var rootIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementingMethod)
                .setTarget(psiClass)
                .setTooltipText(psiClass.getQualifiedName());
        result.add(rootIconBuilder.createLineMarkerInfo(mapper.getXmlTag().getNavigationElement()));

        Consumer<Statement> consumer = statement -> {
            if (statement.getXmlTag() == null) {
                return;
            }
            var methodAttributeValue = statement.getId();
            var psiMethod = methodAttributeValue.getValue();
            if (psiMethod == null) {
                return;
            }
            NavigationGutterIconBuilder<PsiElement> subIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementingMethod)
                    .setTarget(psiMethod)
                    .setTooltipText(psiMethod.getName());
            result.add(subIconBuilder.createLineMarkerInfo(statement.getXmlTag().getFirstChild()));
        };
        Optional.ofNullable(mapper.getSelects()).orElse(Collections.emptyList()).forEach(consumer);
        Optional.ofNullable(mapper.getDeletes()).orElse(Collections.emptyList()).forEach(consumer);
        Optional.ofNullable(mapper.getInserts()).orElse(Collections.emptyList()).forEach(consumer);
        Optional.ofNullable(mapper.getUpdates()).orElse(Collections.emptyList()).forEach(consumer);
    }
}
