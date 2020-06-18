package io.github.cdgeass.dialog;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdgeass
 * @since 2020-06-17
 */
public class FormatToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var formatToolWindow = new FormatToolWindow(project);
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(formatToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}

