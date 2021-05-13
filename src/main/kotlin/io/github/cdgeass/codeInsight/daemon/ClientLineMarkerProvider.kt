package io.github.cdgeass.codeInsight.daemon

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType
import com.intellij.xml.util.XmlPsiUtil
import io.github.cdgeass.codeInsight.util.findByNamespace

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
        if (element !is PsiIdentifier) {
            return
        }

        val lineMarkerInfo: RelatedItemLineMarkerInfo<PsiElement> = when (val parent = element.parent) {
            is PsiClass -> {
                collectTargets(element, parent)
            }
            is PsiMethod -> {
                collectTarget(element, parent)
            }
            else -> {
                null
            }
        } ?: return

        result.add(lineMarkerInfo)
    }

    /**
     * 接口
     */
    private fun collectTargets(element: PsiIdentifier, parent: PsiClass): RelatedItemLineMarkerInfo<PsiElement>? {
        val qualifiedName = parent.qualifiedName ?: return null
        val xmlFiles = findByNamespace(qualifiedName, element.project)

        val targets = xmlFiles.map { xmlFile ->
            val xmlTokens = mutableListOf<XmlToken>()
            XmlPsiUtil.processXmlElementChildren(
                xmlFile,
                { psiElement ->
                    if (psiElement is XmlToken &&
                        psiElement.prevSibling.elementType == XmlTokenType.XML_START_TAG_START &&
                        psiElement.parent is XmlTag &&
                        psiElement.text == "mapper"
                    ) {
                        xmlTokens.add(psiElement)
                    }
                    true
                },
                true
            )
            xmlTokens
        }.flatten()
        if (targets.isEmpty()) {
            return null
        }

        return NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
            .setTargets(targets)
            .createLineMarkerInfo(element)
    }

    /**
     * 方法
     */
    private fun collectTarget(element: PsiIdentifier, parent: PsiMethod): RelatedItemLineMarkerInfo<PsiElement>? {
        val psiClass = (PsiTreeUtil.findFirstParent(parent) { it is PsiClass } ?: return null) as PsiClass
        val qualifiedName = psiClass.qualifiedName ?: return null

        val xmlFiles = findByNamespace(qualifiedName, element.project)

        val targets = xmlFiles.map { xmlFile ->
            val xmlTokens = mutableListOf<XmlToken>()
            XmlPsiUtil.processXmlElementChildren(
                xmlFile,
                { psiElement ->
                    if (psiElement is XmlToken &&
                        psiElement.prevSibling.elementType == XmlTokenType.XML_START_TAG_START &&
                        psiElement.parent is XmlTag &&
                        (psiElement.parent as XmlTag).getAttributeValue("id") == element.text
                    ) {
                        xmlTokens.add(psiElement)
                    }
                    true
                },
                true
            )
            xmlTokens
        }.flatten()
        if (targets.isEmpty()) {
            return null
        }

        return NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
            .setTargets(targets)
            .createLineMarkerInfo(element)
    }
}
