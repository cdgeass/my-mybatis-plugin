package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-18
 */
class CommentGeneratorState : BaseState() {

    var properties by map<String, String>()

}