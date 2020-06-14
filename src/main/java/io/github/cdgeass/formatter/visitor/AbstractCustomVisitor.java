package io.github.cdgeass.formatter.visitor;

import io.github.cdgeass.constants.StringConstants;

/**
 * @author cdgeass
 * @since 2020-06-12
 */
public abstract class AbstractCustomVisitor {

    private final int level;
    private final String tabCharacter;

    private final StringBuilder sqlStringBuilder;

    protected AbstractCustomVisitor(int level) {
        this.level = level;
        this.tabCharacter = StringConstants.TAB_CHARACTER.repeat(Math.max(0, level));
        this.sqlStringBuilder = new StringBuilder(tabCharacter);
    }

    protected AbstractCustomVisitor append(String statement) {
        sqlStringBuilder.append(statement);
        return this;
    }

    protected AbstractCustomVisitor appendTab() {
        sqlStringBuilder.append(tabCharacter);
        return this;
    }

    protected int currentLevel() {
        return level;
    }

    protected int nextLevel() {
        return level + 1;
    }

    @Override
    public String toString() {
        return sqlStringBuilder.toString();
    }
}
