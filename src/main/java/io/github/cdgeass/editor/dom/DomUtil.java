package io.github.cdgeass.editor.dom;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import io.github.cdgeass.constants.StringConstants;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-08-13
 */
@UtilityClass
public class DomUtil extends com.intellij.util.xml.DomUtil {

    public <T extends DomElement> T findFileElement(PsiElement element, Class<T> domClass) {
        if (!(element instanceof XmlFile)) {
            return null;
        }
        var domManager = DomManager.getDomManager(element.getProject());
        var domFileElement = domManager.getFileElement((XmlFile) element, domClass);
        if (domFileElement == null) {
            return null;
        }
        return domFileElement.getRootElement();
    }

    public String getContainingFileNameSpace(PsiElement element) {
        var psiFile = element.getContainingFile();
        if (!(psiFile instanceof XmlFile)) {
            return null;
        }

        var xmlTag = PsiTreeUtil.findChildOfType(psiFile, XmlTag.class);
        if (xmlTag == null) {
            return null;
        }

        return xmlTag.getAttributeValue(StringConstants.NAMESPACE);
    }

    public List<XmlFile> findByNamespace(String namespace, Project project) {
        if (StringUtils.isBlank(namespace) || project == null) {
            return Collections.emptyList();
        }

        var cacheManager = CacheManager.getInstance(project);
        var files = cacheManager.getFilesWithWord(namespace, UsageSearchContext.ANY,
                GlobalSearchScope.allScope(project), true);
        if (ArrayUtils.isEmpty(files)) {
            return Collections.emptyList();
        }

        return Arrays.stream(files)
                .filter(file -> file instanceof XmlFile)
                .map(file -> (XmlFile) file)
                .filter(xmlFile -> {
                    var rootTag = xmlFile.getRootTag();
                    return rootTag != null && Objects.equals(namespace, rootTag.getAttributeValue(StringConstants.NAMESPACE));
                })
                .collect(Collectors.toList());
    }

    public <T extends DomElement> List<T> findByNamespace(String namespace, Project project, Class<T> domClass) {
        return findByNamespace(namespace, project)
                .stream()
                .map(xmlFile -> findFileElement(xmlFile, domClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<XmlTag> findByNameInNamespace(String namespace, Project project, String name) {
        var xmlFiles = findByNamespace(namespace, project);
        return xmlFiles.stream()
                .flatMap(xmlFile -> PsiTreeUtil.findChildrenOfType(xmlFile, XmlTag.class).stream())
                .filter(xmlTag -> name.equals(xmlTag.getName()))
                .collect(Collectors.toList());
    }
}
