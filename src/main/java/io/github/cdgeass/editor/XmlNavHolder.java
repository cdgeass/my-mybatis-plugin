package io.github.cdgeass.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
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
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-05-16
 */
public class XmlNavHolder {

    private static final List<String> ELEMENT_NAMES = Lists.newArrayList("select", "insert", "update", "delete");

    private static final ConcurrentHashMap<String, Set<PsiElement>> XML_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PsiElement> DAO_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, PsiFile> XML_DAO_MAP = new ConcurrentHashMap<>();

    private XmlNavHolder() {
    }

    public static void scan(Project project) {
        var javaVirtualFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        var xmlVirtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));

        if (CollectionUtils.isEmpty(javaVirtualFiles) || CollectionUtils.isEmpty(xmlVirtualFiles)) {
            return;
        }

        var psiManager = PsiManager.getInstance(project);
        var javaFiles = javaVirtualFiles.stream().map(psiManager::findFile).collect(Collectors.toList());
        var xmlFiles = xmlVirtualFiles.stream().map(psiManager::findFile).collect(Collectors.toList());

        for (var xmlFile : xmlFiles) {
            if (!(xmlFile instanceof XmlFile)) {
                continue;
            }
            var document = ((XmlFile) xmlFile).getDocument();
            if (document == null) {
                continue;
            }

            var rootTag = document.getRootTag();
            if (rootTag == null || !"mapper".equals(rootTag.getName())) {
                continue;
            }

            var namespaceAttribute = rootTag.getAttribute("namespace");
            if (namespaceAttribute == null) {
                continue;
            }

            String namespace = namespaceAttribute.getValue();
            if (namespace == null) {
                continue;
            }
            var rootElements = Optional.ofNullable(XML_MAP.get(namespace))
                    .orElseGet(Sets::newHashSet);
            rootElements.add(rootTag);
            XML_MAP.put(namespace, rootElements);

            var subTags = rootTag.getSubTags();
            for (XmlTag subTag : subTags) {
                if (subTag == null || !ELEMENT_NAMES.contains(subTag.getName())) {
                    continue;
                }

                var idAttribute = subTag.getAttribute("id");
                if (idAttribute == null) {
                    continue;
                }

                var id = idAttribute.getValue();
                XML_MAP.put(namespace + "." + id, Sets.newHashSet(subTag));
            }
        }

        for (var javaFile : javaFiles) {
            var clazz = PsiTreeUtil.findChildOfAnyType(javaFile, PsiClass.class);
            if (clazz == null) {
                continue;
            }

            var qualifiedName = clazz.getQualifiedName();
            if (qualifiedName == null || !XML_MAP.containsKey(qualifiedName)) {
                continue;
            }
            DAO_MAP.put(qualifiedName, clazz);


            var methods = PsiTreeUtil.findChildrenOfAnyType(clazz, PsiMethod.class);
            for (var method : methods) {
                if (method == null) {
                    continue;
                }

                var methodName = method.getName();
                if (!XML_MAP.containsKey(qualifiedName + "." + methodName)) {
                    continue;
                }
                DAO_MAP.put(qualifiedName + "." + methodName, method);
            }
        }
    }

    public static List<RelatedItemLineMarkerInfo<PsiElement>> build(PsiJavaFile javaFile) {
        List<RelatedItemLineMarkerInfo<PsiElement>> lineMarkerInfos = Lists.newArrayList();
        var clazz = PsiTreeUtil.findChildOfType(javaFile, PsiClass.class);
        if (clazz == null) {
            return lineMarkerInfos;
        }

        var qualifiedName = clazz.getQualifiedName();
        if (qualifiedName == null || clazz.getNameIdentifier() == null || !XML_MAP.containsKey(qualifiedName)) {
            return lineMarkerInfos;
        }

        List<PsiElement> xmlTags = Lists.newArrayList();
        for (var xmlTag : XML_MAP.get(qualifiedName)) {
            var xmlFile = (XmlFile) PsiTreeUtil.findFirstParent(xmlTag, parent -> parent instanceof XmlFile);
            if (xmlFile == null) {
                continue;
            }
            xmlTags.add(xmlTag);
        }
        NavigationGutterIconBuilder<PsiElement> interfaceIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                .setTargets(xmlTags);
        lineMarkerInfos.add(interfaceIconBuilder.createLineMarkerInfo(clazz.getNameIdentifier()));

        var methods = PsiTreeUtil.findChildrenOfType(clazz, PsiMethod.class);
        for (var method : methods) {
            if (method == null || method.getNameIdentifier() == null || !XML_MAP.containsKey(qualifiedName + "." + method.getName())) {
                continue;
            }

            for (var subXmlTag : XML_MAP.get(qualifiedName + "." + method.getName())) {
                NavigationGutterIconBuilder<PsiElement> methodIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                        .setTarget(subXmlTag.getNavigationElement())
                        .setTooltipText(method.getName());
                lineMarkerInfos.add(methodIconBuilder.createLineMarkerInfo(method.getNameIdentifier()));
            }
        }

        return lineMarkerInfos.stream().distinct().collect(Collectors.toList());
    }

    public static List<RelatedItemLineMarkerInfo<PsiElement>> build(XmlFile xmlFile) {
        List<RelatedItemLineMarkerInfo<PsiElement>> lineMarkerInfos = Lists.newArrayList();

        var document = xmlFile.getDocument();
        if (document == null) {
            return lineMarkerInfos;
        }

        var rootTag = document.getRootTag();
        if (rootTag == null || !"mapper".equals(rootTag.getName())) {
            return lineMarkerInfos;
        }

        var namespaceAttribute = rootTag.getAttribute("namespace");
        if (namespaceAttribute == null) {
            return lineMarkerInfos;
        }
        var namespace = namespaceAttribute.getValue();
        if (namespace == null || !DAO_MAP.containsKey(namespace)) {
            return lineMarkerInfos;
        }

        var dao = DAO_MAP.get(namespace);
        NavigationGutterIconBuilder<PsiElement> rootIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementingMethod)
                .setTarget(dao)
                .setTooltipText(namespace);
        lineMarkerInfos.add(rootIconBuilder.createLineMarkerInfo(rootTag.getFirstChild()));

        var fileName = xmlFile.getName();
        var javaFile = (PsiFile) PsiTreeUtil.findFirstParent(dao, psiElement -> psiElement instanceof PsiFile);
        if (XML_DAO_MAP.containsKey(fileName) && !Objects.equals(XML_DAO_MAP.get(fileName), javaFile)) {
            var originJavaFile = XML_DAO_MAP.get(fileName);
            var clazz = PsiTreeUtil.findChildOfAnyType(originJavaFile, PsiClass.class);
            if (clazz != null && clazz.getQualifiedName() != null) {
                XML_MAP.remove(clazz.getQualifiedName());
                DaemonCodeAnalyzer.getInstance(originJavaFile.getProject()).restart(originJavaFile);
            }
        }
        if (javaFile != null) {
            XML_DAO_MAP.put(fileName, javaFile);
        }

        var subTags = rootTag.getSubTags();
        for (var subTag : subTags) {
            if (subTag == null || !ELEMENT_NAMES.contains(subTag.getName())) {
                continue;
            }

            var idAttribute = subTag.getAttribute("id");
            if (idAttribute == null) {
                continue;
            }
            var id = idAttribute.getValue();
            if (id == null || !DAO_MAP.containsKey(namespace + "." + id)) {
                continue;
            }

            var method = DAO_MAP.get(namespace + "." + id);
            NavigationGutterIconBuilder<PsiElement> subIconBuilder = NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementingMethod)
                    .setTarget(method)
                    .setTooltipText(namespace + "." + id);
            lineMarkerInfos.add(subIconBuilder.createLineMarkerInfo(subTag.getFirstChild()));
        }

        return lineMarkerInfos;
    }

    public static Set<PsiElement> getXmlTag(String key) {
        return XML_MAP.get(key);
    }

    public static PsiElement getDao(String key) {
        return DAO_MAP.get(key);
    }
}
