package io.github.cdgeass.codeInsight.daemon

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTokenType
import com.intellij.util.xml.DomUtil
import io.github.cdgeass.editor.dom.element.mapper.Mapper
import io.github.cdgeass.editor.dom.element.mapper.Statement

/**
 * @author cdgeass
 * @since 2021-03-18
 */
class XmlLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun getName(): String {
        return "MYBATIS XML"
    }

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element.elementType != XmlTokenType.XML_NAME) {
            return
        }

        val lineMarkerInfo: RelatedItemLineMarkerInfo<PsiElement> = when {
            isRootToken(element) -> {
                collectRootTarget(element)
            }
            isStatementToken(element) -> {
                collectTarget(element)
            }
            else -> {
                null
            }
        } ?: return

        result.add(lineMarkerInfo)
    }

    private fun collectRootTarget(element: PsiElement): RelatedItemLineMarkerInfo<PsiElement>? {
        val mapper = DomUtil.findDomElement(element, Mapper::class.java) ?: return null

        val psiClass = mapper.namespace.value ?: return null
        return NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementingMethod)
            .setTarget(psiClass.identifyingElement)
            .createLineMarkerInfo(element)
    }

    private fun collectTarget(element: PsiElement): RelatedItemLineMarkerInfo<PsiElement>? {
        val statement = DomUtil.findDomElement(element, Statement::class.java) ?: return null

        val psiMethod = statement.id.value ?: return null
        val psiClass = PsiTreeUtil.findFirstParent(psiMethod) { psiElement ->
            psiElement is PsiClass
        } ?: return null

        // 如果 xml 指向的是父类方法 直接定位子类上 TODO 增加可选项
        val mapper = DomUtil.findDomElement(element, Mapper::class.java) ?: return null
        val identifier = if (psiClass == mapper.namespace.value) {
            psiMethod.identifyingElement
        } else {
            mapper.namespace.value?.identifyingElement
        } ?: return null

        return NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementingMethod)
            .setTarget(identifier)
            .createLineMarkerInfo(element)
    }

    private fun isRootToken(element: PsiElement): Boolean {
        return element.prevSibling.elementType == XmlTokenType.XML_START_TAG_START && element.text == "mapper"
    }

    private fun isStatementToken(element: PsiElement): Boolean {
        return element.text == "id" && PsiTreeUtil.findFirstParent(element) {
            it is XmlTag && STATEMENT_NAMES.contains(it.name)
        } != null
    }

    companion object {
        private val STATEMENT_NAMES = listOf("select", "update", "delete", "insert")
    }
}