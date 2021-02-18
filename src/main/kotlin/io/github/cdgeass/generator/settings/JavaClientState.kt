package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-18
 */
class JavaClientState : BaseState() {

    var type by string("")

    var properties by map<String, String>()

}