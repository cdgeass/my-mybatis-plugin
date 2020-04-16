package io.github.cdgeass.dialog;

import javax.swing.*;

/**
 * @author cdgeass
 * @since  2020-04-15
 */
public class FormatSelectionWithParamDialog {

    private final String formattedSql;

    private JPanel centerPane;
    private JScrollPane formattedSqlPane;
    private JTextArea formattedSqlTextArea;

    public FormatSelectionWithParamDialog(String formattedSql) {
        this.formattedSql = formattedSql;

        init();
    }

    public void init() {
        formattedSqlTextArea.setText(formattedSql);
    }

    public JPanel getContentPanel() {
        return centerPane;
    }
}
