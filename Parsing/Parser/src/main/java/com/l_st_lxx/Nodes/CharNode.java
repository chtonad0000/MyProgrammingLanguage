package com.l_st_lxx.Nodes;

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
}
