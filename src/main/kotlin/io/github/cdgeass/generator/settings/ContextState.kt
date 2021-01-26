package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since  2021-01-26
 */
class ContextState : BaseState() {

    var defaultModelType by string("")

    var targetRuntime by string("")

}

