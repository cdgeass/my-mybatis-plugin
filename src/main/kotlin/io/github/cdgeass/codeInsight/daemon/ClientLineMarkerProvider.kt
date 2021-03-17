package io.github.cdgeass.codeInsight.daemon

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType
import com.intellij.xml.util.XmlPsiUtil
import io.github.cdgeass.editor.dom.DomUtil

/**
 * @author cdgeass
 * @since 2021-03-17
 */
class ClientLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun getName(): String {
        return "MYBATIS CLIENT"
    }

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val parent = element.parent
        if (element !is PsiIdentifier || parent !is PsiClass) {
            return
        }

        val targets = collectTargets(element, parent)
        if (targets.isEmpty()) {
            return
        }

        result.add(
            NavigationGutterIconBuilder.create(AllIcons.Gutter.OverridenMethod)
                .setTargets(targets)
                .createLineMarkerInfo(element)
        )
    }

    private fun collectTargets(element: PsiIdentifier, parent: PsiClass): List<PsiElement> {
        val qualifiedName = parent.qualifiedName ?: return listOf()
        val xmlFiles = DomUtil.findByNamespace(qualifiedName, element.project)

        return xmlFiles.mapNotNull { xmlFile ->
            val xmlTokens = mutableListOf<XmlToken>()
            XmlPsiUtil.processXmlElementChildren(
                xmlFile,
                { psiElement ->
                    // 是否 mapper 标签
                    if (psiElement is XmlToken && psiElement.text == "mapper" && psiElement.parent is XmlTag) {
                        // 是否开始标签
                        if (psiElement.prevSibling.elementType == XmlTokenType.XML_START_TAG_START) {
                            xmlTokens.add(psiElement)
                        }
                    }
                    true
                },
                true
            )
            xmlTokens
        }.flatten().distinct()
    }

}