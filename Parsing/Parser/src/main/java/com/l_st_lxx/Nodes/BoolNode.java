package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class BoolNode extends ASTNode {
    private final boolean value;

    public BoolNode(String value) {
        this.value = Boolean.parseBoolean(value);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void print() {
        System.out.print(value ? "true" : "false");
    }

    @Override
    public String toPrettyString(int indentLevel) {
        return getIndent(indentLevel) + (value ? "true" : "false");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.BOOL;
    }
}
