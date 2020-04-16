package io.github.cdgeass.dialog;

import javax.swing.*;

/**
 * @author cdgeass
 * @since  2020-04-15
 */
public class FormatSelectionWithParamDialog {

    private final String sql;
    private final String formattedSql;

    private JPanel centerPane;
    private JTextArea sqlTextArea;
    private JScrollPane sqlPane;
    private JScrollPane formattedSqlPane;
    private JTextArea formattedSqlTextArea;

    public FormatSelectionWithParamDialog(String sql, String formattedSql) {
        this.sql = sql;
        this.formattedSql = formattedSql;

        init();
    }

    public void init() {
        sqlTextArea.setText(sql);
        formattedSqlTextArea.setText(formattedSql);
    }

    public JPanel getContentPanel() {
        return centerPane;
    }
}
