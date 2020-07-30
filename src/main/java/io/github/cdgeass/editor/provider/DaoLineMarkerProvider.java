package io.github.cdgeass.editor.provider;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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

        var psiMethods = Lists.newArrayList(psiClass.getMethods());

        var qualifiedName = psiClass.getQualifiedName();
        var domManager = DomManager.getDomManager(element.getProject());
        var cacheManager = CacheManager.SERVICE.getInstance(element.getProject());

        var psiFiles = cacheManager.getFilesWithWord(qualifiedName, UsageSearchContext.ANY,
                GlobalSearchScope.projectScope(element.getProject()), true);
        var mappers = Arrays.stream(psiFiles)
                .filter(psiFile -> psiFile instanceof XmlFile)
                .map(psiFile -> domManager.getFileElement((XmlFile) psiFile, Mapper.class))
                .filter(Objects::nonNull)
                .map(DomFileElement::getRootElement)
                .filter(mapper -> ObjectUtils.equals(mapper.getNamespace().getValue(), psiClass))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(mappers)) {
            return;
        }
        NavigationGutterIconBuilder<PsiElement> interfaceIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                .setTargets(mappers.stream().map(Mapper::getXmlTag).collect(Collectors.toList()));
        result.add(interfaceIconBuilder.createLineMarkerInfo(psiClass.getNameIdentifier()));

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
