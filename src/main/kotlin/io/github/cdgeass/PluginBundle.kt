package io.github.cdgeass

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

/**
 * @author cdgeass
 * @since 2021-03-10
 */
class PluginBundle : DynamicBundle(BUNDLE) {

    companion object {
        private const val BUNDLE = "messages.MyMyBatisBundle"

        private val INSTANCE = PluginBundle()

        fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
            return if (INSTANCE.containsKey(key)) {
                INSTANCE.getMessage(key, *params)
            } else ""
        }
    }

}