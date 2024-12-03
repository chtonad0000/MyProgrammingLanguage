package com.l_st_lxx.Nodes;

public class NumberNode extends ASTNode {
    private final String value;

    public NumberNode(String value) {
        this.value = value;
    }

    public String getValue() {
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
}
