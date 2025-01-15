package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class NumberNode extends ASTNode {
    private final int value;

    public NumberNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void print() {
        System.out.print(value);
    }

    @Override
    public String toPrettyString(int indentLevel) {
        return getIndent(indentLevel) + value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.NUMBER;
    }
}
