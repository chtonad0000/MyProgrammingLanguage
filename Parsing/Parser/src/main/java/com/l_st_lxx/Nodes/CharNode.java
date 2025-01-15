package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class CharNode extends ASTNode {
    private final char value;

    public CharNode(String value) {
        this.value = value.charAt(1);
    }

    public char getValue() {
        return value;
    }

    @Override
    public void print() {
        System.out.print("'" + value + "'");
    }

    @Override
    public String toPrettyString(int indentLevel) {
        return getIndent(indentLevel) + "'" + value + "'";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.CHAR;
    }
}
