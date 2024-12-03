package com.l_st_lxx.Nodes;

public abstract class ASTNode {
    public abstract void print();

    public abstract String toPrettyString(int indentLevel);
    protected String getIndent(int indentLevel) {
        return "  ".repeat(indentLevel);
    }
}
