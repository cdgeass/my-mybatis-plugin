package io.github.cdgeass.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import io.github.cdgeass.formatter.WithParamFormatter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * @author cdgeass
 * @since 2020-04-21
 */
public class FormatAndCopySelectionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        var selectionModel = editor.getSelectionModel();
        if (StringUtils.isBlank(selectionModel.getSelectedText())) {
            return;
        }
        var systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = WithParamFormatter.format(selectionModel.getSelectedText());
        systemClipboard.setContents(new StringSelection(selection), null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null || selectedText.length() == 0) {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
