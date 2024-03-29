package io.github.cdgeass.generator.action

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import io.github.cdgeass.PluginBundle
import org.mybatis.generator.api.ProgressCallback

/**
 * @author cdgeass
 * @since 2021-03-04
 */
class GenerateProgressCallback(
    private val warnings: List<String>
) : ProgressCallback {

    override fun introspectionStarted(totalTasks: Int) {
        print("")
    }

    override fun generationStarted(totalTasks: Int) {
        print("")
    }

    override fun saveStarted(totalTasks: Int) {
        print("")
    }

    override fun startTask(taskName: String?) {
        print("")
    }

    override fun done() {
        VirtualFileManager.getInstance().syncRefresh()
        if (warnings.isEmpty()) {
            Messages.showInfoMessage(PluginBundle.message("generator.success"), PluginBundle.message("generator.title"))
        } else {
            Messages.showInfoMessage(warnings.joinToString("\n"), PluginBundle.message("generator.title"))
        }
    }

    override fun checkCancel() {
        print("")
    }
}
