package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.containers.SoftFactoryMap
import com.intellij.util.xml.ConvertContext
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomFileDescription
import com.intellij.util.xml.DomManager
import com.intellij.util.xml.DomUtil
import com.intellij.util.xml.ElementPresentationManager
import com.intellij.util.xml.GenericDomValue
import com.intellij.util.xml.ResolvingConverter
import io.github.cdgeass.codeInsight.dom.description.MergingMapperDescription
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.util.findByNamespace
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author cdgeass
 * @since 2021-06-28
 */
class MyDomResolveConverter : ResolvingConverter<DomElement>() {

    private val myResolveCache =
        object : SoftFactoryMap<DomElement, CachedValue<Map<Pair<Type, String>, DomElement>>>() {
            override fun create(scope: DomElement?): CachedValue<Map<Pair<Type, String>, DomElement>> {
                val domManager = scope?.manager ?: throw AssertionError("Null DomManager for " + scope!!.javaClass)
                val project = domManager.project
                return CachedValuesManager.getManager(project)
                    .createCachedValue(
                        object : CachedValueProvider<Map<Pair<Type, String>, DomElement>> {
                            override fun compute(): CachedValueProvider.Result<Map<Pair<Type, String>, DomElement>> {
                                val map = mutableMapOf<Pair<Type, String>, DomElement>()
                                visitDomElement(scope, map)
                                return CachedValueProvider.Result(map, PsiModificationTracker.MODIFICATION_COUNT)
                            }

                            private fun visitDomElement(
                                element: DomElement,
                                map: MutableMap<Pair<Type, String>, DomElement>
                            ) {
                                val name = ElementPresentationManager.getElementName(element)
                                val type = element.domElementType
                                val key = Pair(type, name ?: "")
                                if (name != null && !map.containsKey(key)) {
                                    map[key] = element
                                } else {
                                    for (child in DomUtil.getDefinedChildren(element, true, true))
                                        visitDomElement(child, map)
                                }
                            }
                        },
                        false
                    )
            }
        }

    override fun toString(element: DomElement?, context: ConvertContext): String? {
        if (element == null) return null
        return ElementPresentationManager.getElementName(element)
    }

    override fun fromString(s: String?, context: ConvertContext): DomElement? {
        if (s == null) return null
        val scope = getResolvingScope(s, context) ?: return null
        val type = (context.invocationElement.domElementType as ParameterizedType).actualTypeArguments[0]
        return if (s.contains(".")) {
            myResolveCache.get(scope).value[Pair(type, s.substringAfterLast("."))]
        } else {
            myResolveCache.get(scope).value[Pair(type, s)]
        }
    }

    override fun getVariants(context: ConvertContext): MutableCollection<out DomElement> {
        val reference = context.invocationElement
        val scope = reference.manager.getResolvingScope(reference as GenericDomValue<*>)
        val type = (context.invocationElement.domElementType as ParameterizedType).actualTypeArguments[0]
        return myResolveCache[scope].value.filterKeys { key -> key.first == type }
            .values as MutableCollection<out DomElement>
    }

    private fun getResolvingScope(s: String, context: ConvertContext): DomElement? {
        return if (s.contains(".")) {
            val namespace = s.substringBeforeLast(".")
            val domManager = DomManager.getDomManager(context.project)
            val xmlFile = findByNamespace(namespace, context.project).first()
            val fileDescription: DomFileDescription<*>? = domManager.getDomFileDescription(xmlFile)
            (fileDescription as MergingMapperDescription).getMergedRoot(
                domManager.getFileElement(
                    xmlFile,
                    Mapper::class.java
                )
            )
        } else {
            val invocationElement = context.invocationElement
            invocationElement.manager.getResolvingScope(invocationElement as GenericDomValue<*>)
        }
    }
}
