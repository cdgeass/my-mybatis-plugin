package io.github.cdgeass.dialog;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import io.github.cdgeass.constants.MyIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdgeass
 * @since 2020-06-17
 */
public class FormatToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var contentManager = toolWindow.getContentManager();
        var content = contentManager.getFactory().createContent(new FormatToolWindow(project).getContent(),
                "Log Formatter", false);
        contentManager.addContent(content);
    }
}

