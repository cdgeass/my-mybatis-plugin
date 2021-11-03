package io.github.cdgeass.codeInsight

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtil.DUMMY_IDENTIFIER_TRIMMED
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference
import com.intellij.psi.impl.source.tree.injected.InjectedCaret
import com.intellij.psi.xml.XmlFile
import com.intellij.sql.psi.SqlFile
import com.intellij.sql.psi.impl.SqlTokenElement
import io.github.cdgeass.codeInsight.reference.MyLiteralExpressionReference
import io.github.cdgeass.util.createLookupElement

/**
 * @author cdgeass
 * @since 2021/4/1
 */
class MyLiteralExpressionCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val reference = findReference(parameters)

        if (reference != null) {
            val context = reference.context
            if (context != null && parameters.position !is SqlTokenElement) {
                reference.processSubElement(
                    context,
                    result
                )
            } else {
                reference.contextResolve()?.apply {
                    reference.processSubElement(
                        this,
                        result.withPrefixMatcher("")
                    )
                } ?: findElement(parameters)?.apply {
                    val map = MyLiteralExpressionResolver(this).resolve()
                    map.forEach { (name, param) ->
                        result.withPrefixMatcher(getOriginText(reference.myText)).consume(createLookupElement(name, param.ref))
                    }
                }
            }
        } else {
            findElement(parameters)?.apply {
                val map = MyLiteralExpressionResolver(this).resolve()
                map.forEach { (name, param) ->
                    result.withPrefixMatcher(getOriginText(parameters.position.text)).consume(createLookupElement(name, param.ref))
                }
            }
        }
    }

    private fun getOriginText(text: String): String {
        return text.substringBefore(DUMMY_IDENTIFIER_TRIMMED)
    }

    private fun findReference(parameters: CompletionParameters): MyLiteralExpressionReference? {
        val position = parameters.position

        var file = position.containingFile
        var reference: PsiReference? = null
        if (file is SqlFile) {
            // 启用 database 插件后 sql 会成为 sqlFile 子文件 position 的 offset 是相对 sql file 的
            val virtualFile = parameters.originalFile.virtualFile
            if (virtualFile !is VirtualFileWindow) {
                return null
            }
            file = PsiManager.getInstance(position.project).findFile(virtualFile.delegate) ?: return null

            val curCaret = parameters.editor.caretModel.currentCaret
            if (curCaret !is InjectedCaret) {
                return null
            }

            reference = file.findReferenceAt(curCaret.delegate.offset - 1)
        } else {
            reference = file.findReferenceAt(parameters.offset)
        }

        (reference as? PsiMultiReference)?.references?.forEach {
            if (it is MyLiteralExpressionReference) {
                return it
            }
        }

        return reference as? MyLiteralExpressionReference
    }

    private fun findElement(parameters: CompletionParameters): PsiElement? {
        val position = parameters.position

        var file = position.containingFile
        return if (file is XmlFile) {
            position
        } else {
            // 启用 database 插件后 sql 会成为 sqlFile 子文件 position 的 offset 是相对 sql file 的
            val virtualFile = parameters.originalFile.virtualFile
            if (virtualFile !is VirtualFileWindow) {
                return null
            }
            file = PsiManager.getInstance(position.project).findFile(virtualFile.delegate) ?: return null

            val curCaret = parameters.editor.caretModel.currentCaret
            if (curCaret !is InjectedCaret) {
                return null
            }

            file.findElementAt(curCaret.delegate.offset - 1)
        }
    }
}
