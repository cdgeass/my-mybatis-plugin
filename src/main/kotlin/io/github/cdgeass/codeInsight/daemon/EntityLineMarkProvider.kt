package io.github.cdgeass.codeInsight.daemon

import com.google.common.base.CaseFormat
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.database.psi.DbElement
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.database.view.DbNavigationUtils
import com.intellij.icons.AllIcons
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiKeyword
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import icons.DatabaseIcons
import io.github.cdgeass.codeInsight.reference.MyJavaClassReference
import org.jetbrains.annotations.Nls
import java.util.function.Function
import java.util.function.Supplier
import javax.swing.Icon

/**
 * @author cdgeass
 * @since 2021-09-09
 */
class EntityLineMarkProvider : LineMarkerProviderDescriptor() {

    override fun getName(): String {
        return "MYBATIS ENTITY"
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        for (element in elements) {
            if (!isClassIdentifier(element)) {
                continue
            }

            if (!isEntity(element)) {
                continue
            }

            collectDbElementList(element).forEach { dbElement ->
                val relatedItemLineMarkerInfo = MyLineMarkerInfo(element, element.textRange, dbElement)
                result.add(relatedItemLineMarkerInfo)
            }
        }
    }

    /**
     * element 是不是类名
     */
    private fun isClassIdentifier(element: PsiElement): Boolean {
        if (element !is PsiIdentifier) {
            return false
        }
        var preElement = element.prevSibling
        if (preElement is PsiWhiteSpace) {
            preElement = preElement.prevSibling
        }
        return preElement is PsiKeyword && preElement.text == "class"
    }

    /**
     * 是否和 ResultMap 关联
     */
    private fun isEntity(element: PsiElement): Boolean {
        val classElement = element.parent
        if (classElement !is PsiClass) {
            return false
        }

        return ReferencesSearch.search(classElement)
            .anyMatch { it ->
                if (it !is MyJavaClassReference) {
                    return@anyMatch false
                }
                val parent = PsiTreeUtil.findFirstParent(it.element) { it is XmlTag }?.let { it as XmlTag }
                return@anyMatch parent?.name == "resultMap"
            }
    }

    /**
     * 创建跳转到 DatabaseView
     */
    private fun collectDbElementList(element: PsiElement): List<DbElement> {
        val project = element.project
        val entityName = element.text

        val settings = project.getService(io.github.cdgeass.generator.settings.Settings::class.java)

        val dbElementList = mutableListOf<DbElement>()

        val dataSources = DbUtil.getDataSources(project)
        for (dataSource in dataSources) {
            val tables = DasUtil.getTables(dataSource)
            for (table in tables) {
                val generatedModelName = settings.modelNamePattern.let { it.ifBlank { "%s" } }
                    .format(
                        CaseFormat.LOWER_UNDERSCORE.to(
                            CaseFormat.UPPER_CAMEL,
                            table.name
                        )
                    )
                if (entityName == generatedModelName) {
                    DbImplUtil.findElement(dataSource, table)?.apply {
                        dbElementList.add(this)
                    }

                }
            }
        }
        return dbElementList
    }

    companion object {
        private class MyLineMarkerInfo(
            element: PsiElement,
            textRange: TextRange,
            dbElement: DbElement
        ) : MergeableLineMarkerInfo<PsiElement>(
            element,
            textRange,
            DatabaseIcons.Table,
            { dbElement.text },
            { _, _ -> DbNavigationUtils.navigateToDatabaseView(dbElement, true) },
            GutterIconRenderer.Alignment.RIGHT,
            { dbElement.text }
        ) {

            override fun canMergeWith(info: MergeableLineMarkerInfo<*>): Boolean {
                return info is Companion.MyLineMarkerInfo && myIcon == info.icon
            }

            override fun getCommonIcon(infos: MutableList<out MergeableLineMarkerInfo<*>>): Icon {
                return DatabaseIcons.Dbms
            }

        }
    }
}