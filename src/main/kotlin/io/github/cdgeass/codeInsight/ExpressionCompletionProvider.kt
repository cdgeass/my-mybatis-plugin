package io.github.cdgeass.codeInsight

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiField
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.impl.source.tree.injected.InjectedCaret
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTokenType
import com.intellij.sql.dialects.SqlLanguageDialect
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import io.github.cdgeass.codeInsight.reference.ParamReference

/**
 * @author cdgeass
 * @since 2021/4/1
 */
class ExpressionCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.language !is SqlLanguageDialect) {
            return
        }
        if (position.prevSibling?.text != "#{" && position.prevSibling?.text != "\${") {
            return
        }

        var file = position.containingFile.virtualFile
        if (file !is VirtualFileWindow) {
            return
        }
        file = file.delegate

        val xmlFile = PsiManager.getInstance(position.project).findFile(file)
        if (xmlFile !is XmlFile) {
            return
        }

        var caret = parameters.editor.caretModel.currentCaret
        if (caret is InjectedCaret) {
            caret = caret.delegate
        }

        val element = xmlFile.findElementAt(caret.offset) ?: return
        if (element.elementType != XmlTokenType.XML_DATA_CHARACTERS) {
            return
        }

        val expression = element.text.substring(2).substringBeforeLast("}")
        val reference = if (expression.contains(".")) {
            val subExpression = expression.substringBeforeLast(".")
            ParamReference(element, TextRange(3 + subExpression.length, 2 + expression.length), subExpression)
        } else {
            ParamReference(element, TextRange(2, 2 + expression.length), "")
        }
        val variants = reference.variants
        if (variants.isNotEmpty()) {
            val noPrefixResult = result.withPrefixMatcher(reference.myKey)
            variants.forEach {
                if (it is LookupElement) {
                    noPrefixResult.addElement(it)
                } else if (it is PsiNamedElement) {
                    noPrefixResult.addElement(
                        LookupElementBuilder.create(it, it.name ?: "")
                            .withIcon(
                                when (it) {
                                    is PsiMethod -> PlatformIcons.METHOD_ICON
                                    is PsiField -> PlatformIcons.FIELD_ICON
                                    else -> null
                                }
                            )
                            .withAutoCompletionPolicy(AutoCompletionPolicy.SETTINGS_DEPENDENT)
                    )
                }
            }
            noPrefixResult.stopHere()
        }
    }
}
