package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-14
 */
class JavaTypeResolverState : BaseState() {

    var properties by map<String, String>()

}