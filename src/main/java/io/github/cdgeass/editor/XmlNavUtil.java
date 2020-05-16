package io.github.cdgeass.editor;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since  2020-05-16
 */
public class XmlNavUtil {

    private static final List<String> ELEMENT_NAMES = Lists.newArrayList("select", "insert", "update", "delete");

    private static final ConcurrentHashMap<String, PsiElement> XML_TAG_MAP = new ConcurrentHashMap<>();

    private XmlNavUtil() {}

    public static List<RelatedItemLineMarkerInfo<PsiElement>> findByScan(Project project) {
        var javaVirtualFiles = FileBasedIndex.getInstance()
                .getContainingFiles(FileTypeIndex.NAME, JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        var xmlVirtualFiles = FileBasedIndex.getInstance()
                .getContainingFiles(FileTypeIndex.NAME, XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));

        if (CollectionUtils.isEmpty(javaVirtualFiles) || CollectionUtils.isEmpty(xmlVirtualFiles)) {
            return Collections.emptyList();
        }

        var psiManager = PsiManager.getInstance(project);
        var javaFiles = javaVirtualFiles.stream().map(psiManager::findFile).collect(Collectors.toList());
        var xmlFiles = xmlVirtualFiles.stream().map(psiManager::findFile).collect(Collectors.toList());

        for (var xmlFile : xmlFiles) {
            var document = ((XmlFile) xmlFile).getDocument();
            if (document == null) {
                continue;
            }

            var rootTag = document.getRootTag();
            if (rootTag == null || !rootTag.getName().equals("mapper")) {
                continue;
            }

            var namespaceAttribute = rootTag.getAttribute("namespace");
            if (namespaceAttribute == null) {
                continue;
            }

            // namespace
            String namespace = namespaceAttribute.getValue();
            if (namespace == null) {
                continue;
            }
            XML_TAG_MAP.put(namespace, rootTag);

            var subTags = rootTag.getSubTags();
            if (subTags == null) {
                continue;
            }

            for (XmlTag subTag : subTags) {
                if (subTag == null || !ELEMENT_NAMES.contains(subTag.getName())) {
                    continue;
                }

                var idAttribute = subTag.getAttribute("id");
                if (idAttribute == null) {
                    continue;
                }

                var id = idAttribute.getValue();
                XML_TAG_MAP.put(namespace + "." + id, subTag);
            }
        }

        List<RelatedItemLineMarkerInfo<PsiElement>> lineMarkerInfos = Lists.newArrayList();
        for (var javaFile : javaFiles) {
            var clazz = PsiTreeUtil.findChildOfType(javaFile, PsiClass.class);
            if (clazz == null) {
                continue;
            }

            var qualifiedName = clazz.getQualifiedName();
            if (qualifiedName == null || !XML_TAG_MAP.containsKey(qualifiedName)) {
                continue;
            }

            var methods = PsiTreeUtil.findChildrenOfType(clazz, PsiMethod.class);
            for (var method : methods) {
                if (method == null || method.getNameIdentifier() == null || !XML_TAG_MAP.containsKey(qualifiedName + "." + method.getName())) {
                    continue;
                }
                var xmlTag = XML_TAG_MAP.get(qualifiedName + "." + method.getName());

                NavigationGutterIconBuilder<PsiElement> methodIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                        .setTarget(xmlTag.getNavigationElement())
                        .setTooltipText(qualifiedName);
                lineMarkerInfos.add(methodIconBuilder.createLineMarkerInfo(method.getNameIdentifier()));
            }
        }

        return lineMarkerInfos;
    }
}
