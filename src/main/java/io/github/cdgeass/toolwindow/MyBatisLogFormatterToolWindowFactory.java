package io.github.cdgeass.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdgeass
 * @since 2020-04-14
 */
public class MyBatisLogFormatterToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // init tool window
        var myBatisToolWindow = new MyBatisLogFormatterToolWindow();

        // register tool window
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(myBatisToolWindow.getContent(), "MyBatis", false);
        toolWindow.getContentManager().addContent(content);
    }
}
