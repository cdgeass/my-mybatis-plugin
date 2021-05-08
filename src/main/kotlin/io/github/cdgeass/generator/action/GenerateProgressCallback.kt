package io.github.cdgeass.generator.action

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import io.github.cdgeass.PluginBundle
import org.mybatis.generator.api.ProgressCallback

/**
 * @author cdgeass
 * @since 2021-03-04
 */
class GenerateProgressCallback : ProgressCallback {

    override fun introspectionStarted(totalTasks: Int) {
    }

    override fun generationStarted(totalTasks: Int) {
    }

    override fun saveStarted(totalTasks: Int) {
    }

    override fun startTask(taskName: String?) {
    }

    override fun done() {
        VirtualFileManager.getInstance().syncRefresh()
        Messages.showInfoMessage(PluginBundle.message("generator.success"), PluginBundle.message("generator.title"))
    }

    override fun checkCancel() {
    }

}