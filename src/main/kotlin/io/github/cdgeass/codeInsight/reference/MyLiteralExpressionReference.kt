package io.github.cdgeass.codeInsight.reference

import com.intellij.analysis.AnalysisBundle
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.resolve.ResolveCache.PolyVariantContextResolver
import com.intellij.psi.impl.source.resolve.reference.impl.GenericReference
import com.intellij.util.Consumer
import io.github.cdgeass.codeInsight.MyLiteralExpressionReferenceProvider
import io.github.cdgeass.codeInsight.MyLiteralExpressionResolver
import io.github.cdgeass.codeInsight.getAllSubElement
import io.github.cdgeass.util.createLookupElement

/**
 * @author cdgeass
 * @since 2021-11-02
 */
class MyLiteralExpressionReference(
    private val myLiteralExpressionReferenceSet: MyLiteralExpressionReferenceSet,
    private val myElement: PsiElement,
    private val myTextRange: TextRange,
    private val myStartInElement: Int,
    val myText: String,
    private val myIndex: Int,
    myProvider: MyLiteralExpressionReferenceProvider
) : GenericReference(myProvider), PsiPolyVariantReference {

    fun getReferenceSet(): MyLiteralExpressionReferenceSet {
        return myLiteralExpressionReferenceSet
    }

    override fun getElement(): PsiElement {
        return myLiteralExpressionReferenceSet.getElement()
    }

    override fun getRangeInElement(): TextRange {
        return myTextRange
    }

    override fun getCanonicalText(): String {
        return myText
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return element
    }

    fun contextResolve(): PsiElement? {
        if (myIndex == 0) {
            val contextResolver = MyLiteralExpressionResolver(element)
            val paramNameMap = contextResolver.resolve()
            val type = paramNameMap[myText]?.type
            if (type is PsiClassType) {
                return type.resolve()
            }
            return null
        }
        return resolve()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val resolveResult = advancedResolve(incompleteCode)
        return if (resolveResult == null) ResolveResult.EMPTY_ARRAY else arrayOf(resolveResult)
    }

    private fun advancedResolve(incompleteCode: Boolean): ResolveResult? {
        val file = myElement.containingFile
        val resolveCache = ResolveCache.getInstance(file.project)
        val cachedResolveResults = resolveCache.resolveWithCaching(this, MyResolver.INSTANCE, false, false, file)
        return if (cachedResolveResults.isEmpty()) null else cachedResolveResults[0]
    }

    private fun doAdvancedResolve(containingFile: PsiFile): Array<ResolveResult> {
        val element = element

        if (!element.isValid) return ResolveResult.EMPTY_ARRAY

        if (myIndex == 0) {
            val contextResolver = MyLiteralExpressionResolver(element)
            val paramNameMap = contextResolver.resolve()
            val target = paramNameMap[myText] ?: return ResolveResult.EMPTY_ARRAY
            return PsiElementResolveResult.createResults(target.ref)
        }

        return advancedResolveInner(containingFile)
    }

    private fun advancedResolveInner(containingFile: PsiFile): Array<ResolveResult> {
        val context = context ?: return ResolveResult.EMPTY_ARRAY

        val subElementNameMap = getAllSubElement(context)
        val target = subElementNameMap[myText] ?: return ResolveResult.EMPTY_ARRAY
        return PsiElementResolveResult.createResults(target.ref.navigationElement)
    }

    override fun resolveInner(): PsiElement? {
        return advancedResolve(true)?.element
    }

    override fun getUnresolvedMessagePattern(): String {
        return AnalysisBundle.message("cannot.resolve.symbol")
    }

    override fun getContext(): PsiElement? {
        val contextRef = contextReference
        assert(contextRef !== this) { canonicalText }
        return contextRef?.contextResolve()
    }

    override fun getContextReference(): MyLiteralExpressionReference? {
        return if (myIndex > 0) myLiteralExpressionReferenceSet.getContextReference(myIndex - 1) else null
    }

    private class MyResolver : PolyVariantContextResolver<MyLiteralExpressionReference> {
        override fun resolve(
            ref: MyLiteralExpressionReference,
            containingFile: PsiFile,
            incompleteCode: Boolean
        ): Array<ResolveResult> {
            return ref.doAdvancedResolve(containingFile)
        }

        companion object {
            val INSTANCE = MyResolver()
        }
    }

    fun processSubElement(context: PsiElement, result: Consumer<LookupElement>) {
        val subElementNameMap = getAllSubElement(context)
        subElementNameMap.forEach { (name, subElement) ->
            result.consume(createLookupElement(name, subElement.ref))
        }
    }

    private fun substring(str: String, length: Int): String {
        val substring = str.substring(length)
        val firstChar = substring[0]
        return substring.replaceFirst(firstChar, firstChar.lowercase()[0])
    }

    override fun handleElementRename(newElementName: String): PsiElement? {
        return super.handleElementRename(
            when {
                newElementName.startsWith("is") -> {
                    substring(newElementName, 2)
                }
                newElementName.startsWith("get") -> {
                    substring(newElementName, 3)
                }
                newElementName.startsWith("set") -> {
                    substring(newElementName, 3)
                }
                else -> {
                    newElementName
                }
            }
        )
    }
}