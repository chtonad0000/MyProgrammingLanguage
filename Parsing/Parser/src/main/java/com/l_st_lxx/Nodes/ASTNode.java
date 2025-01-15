package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public abstract class ASTNode {
    public abstract void print();

    public abstract String toPrettyString(int indentLevel);
    protected String getIndent(int indentLevel) {
        return "  ".repeat(indentLevel);
    }

    public abstract void accept(ASTVisitor visitor);

    public abstract NodeType getNodeType();
}
