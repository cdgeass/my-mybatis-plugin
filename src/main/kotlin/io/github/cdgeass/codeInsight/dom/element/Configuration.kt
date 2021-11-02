package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiPackage
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * @author cdgeass
 * @since 2021/3/21
 */
interface Configuration : DomElement {

    @SubTag("typeAliases")
    fun getTypeAliases(): TypeAliases

    @SubTag("settings")
    fun getSettings(): Settings
}

// --- typeAliases

interface TypeAliases : DomElement {

    @SubTagList("typeAlias")
    fun getTypeAliases(): List<TypeAlias>

    @SubTagList("package")
    fun getPackages(): List<Package>
}

interface TypeAlias : DomElement {

    fun getAlias(): GenericAttributeValue<String>

    @Convert(MyPsiClassConverter::class)
    fun getType(): GenericAttributeValue<PsiClass>
}

interface Package : DomElement {

    @Convert(PsiPackageConverter::class)
    fun getName(): GenericAttributeValue<PsiPackage>
}

// --- settings

interface Settings : DomElement {

    fun getUseActualParamName(): GenericAttributeValue<Boolean>
}
