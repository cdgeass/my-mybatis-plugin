package io.github.cdgeass.dialog;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.formatter.WithParamFormatter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class FormatSelectionDialog extends DialogWrapper {

    private final SelectionModel selectionModel;

    public FormatSelectionDialog(SelectionModel selectionModel) {
        super(true);
        this.selectionModel = selectionModel;

        init();
        setTitle("Format Sql");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        var selectedText = selectionModel.getSelectedText();
        if (selectedText == null) {
            return null;
        }

        if (!WithParamFormatter.canFormatter(selectedText)) {
            return null;
        }

        var formattedSql = WithParamFormatter.format(selectedText);
        var sqlEditorTextField = new EditorTextField(formattedSql) {
            @Override
            protected EditorEx createEditor() {
                var editor = super.createEditor();
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                editor.setOneLineMode(false);
                editor.setCaretEnabled(false);

                return editor;
            }
        };

        var editorColorsManager = EditorColorsManager.getInstance();
        var font = editorColorsManager.getSchemeForCurrentUITheme().getFont(EditorFontType.PLAIN);
        sqlEditorTextField.setFont(font);
        sqlEditorTextField.setPreferredSize(new Dimension(500, 450));
        sqlEditorTextField.setCaretPosition(0);

        var project = selectionModel.getEditor().getProject();
        if (project != null) {
            var textAttributes1 = editorColorsManager.getSchemeForCurrentUITheme().getAttributes(EditorColors.DELETED_TEXT_ATTRIBUTES);
            var textAttributes2 = editorColorsManager.getSchemeForCurrentUITheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);

            var highlightManager = HighlightManager.getInstance(project);
            sqlEditorTextField.addSettingsProvider(editor -> {
                var document = editor.getDocument();
                var text = document.getText();
                var emptyLineCount = StringUtils.countMatches(text, StringConstants.EMPTY_LINE);

                var startOffset = 0;
                for (var i = 1; i <= emptyLineCount; i++) {
                    var indexOf = StringUtils.ordinalIndexOf(text, StringConstants.EMPTY_LINE, i);
                    highlightManager.addRangeHighlight(editor, indexOf, indexOf + StringConstants.EMPTY_LINE.length() + 1,
                            textAttributes1, false, null);

                    var textAttributes2Copy = textAttributes2.clone();
                    textAttributes2Copy.setBackgroundColor(i % 2 == 0 ? JBColor.PINK : JBColor.ORANGE);
                    highlightManager.addRangeHighlight(editor, startOffset, indexOf, textAttributes2Copy, false, null);
                    startOffset = indexOf + StringConstants.EMPTY_LINE.length() + 1;
                    if (i == emptyLineCount) {
                        var lastTextAttributes2Copy = textAttributes2.clone();
                        lastTextAttributes2Copy.setBackgroundColor((i + 1) % 2 == 0 ? JBColor.PINK : JBColor.ORANGE);
                        highlightManager.addRangeHighlight(editor, startOffset, text.length(), lastTextAttributes2Copy, false, null);
                    }
                }
            });
        }
        return sqlEditorTextField;
    }


}
