package io.github.cdgeass.editor.provider;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-05-14
 */
public class DaoLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof PsiJavaFile)) {
            return;
        }

        var psiClass = PsiTreeUtil.findChildOfType(element, PsiClass.class);
        if (psiClass == null || psiClass.getQualifiedName() == null || psiClass.getNameIdentifier() == null) {
            return;
        }

        var namespace = psiClass.getQualifiedName();
        var mappers = DomUtil.findByNamespace(namespace, element.getProject(), Mapper.class);
        if (CollectionUtils.isEmpty(mappers)) {
            return;
        }
        NavigationGutterIconBuilder<PsiElement> interfaceIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                .setTargets(mappers.stream().map(Mapper::getXmlTag).collect(Collectors.toList()));
        result.add(interfaceIconBuilder.createLineMarkerInfo(psiClass.getNameIdentifier()));

        var psiMethods = Lists.newArrayList(psiClass.getMethods());
        mappers.stream()
                .flatMap(mapper -> {
                    var selects = Optional.ofNullable(mapper.getSelects()).orElse(Collections.emptyList());
                    var deletes = Optional.ofNullable(mapper.getDeletes()).orElse(Collections.emptyList());
                    var inserts = Optional.ofNullable(mapper.getInserts()).orElse(Collections.emptyList());
                    var updates = Optional.ofNullable(mapper.getUpdates()).orElse(Collections.emptyList());

                    var list = new ArrayList<Statement>();
                    list.addAll(selects);
                    list.addAll(deletes);
                    list.addAll(inserts);
                    list.addAll(updates);
                    return list.stream();
                })
                .forEach(statement -> {
                    var methodAttributeValue = statement.getId();
                    var psiMethod = methodAttributeValue.getValue();
                    if (psiMethod != null && psiMethods.contains(psiMethod) && psiMethod.getNameIdentifier() != null) {
                        NavigationGutterIconBuilder<PsiElement> methodIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                                .setTarget(statement.getXmlTag())
                                .setTooltipText(psiMethod.getName());
                        result.add(methodIconBuilder.createLineMarkerInfo(psiMethod.getNameIdentifier()));
                    }
                });
    }
}
