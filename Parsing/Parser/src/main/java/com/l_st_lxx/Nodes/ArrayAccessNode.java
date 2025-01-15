package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class ArrayAccessNode extends ASTNode {
    private final String arrayName;
    private final ASTNode index;

    public ArrayAccessNode(String arrayName, ASTNode index) {
        this.arrayName = arrayName;
        this.index = index;
    }

    public String getArrayName() {
        return arrayName;
    }

    public ASTNode getIndex() {
        return index;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append("ArrayAccessNode:\n")
                .append(indent).append("  Array Name: ").append(arrayName).append("\n");
        if (index != null) {
            builder.append(indent).append("  Index:\n")
                    .append(index.toPrettyString(indentLevel + 2));
        } else {
            builder.append(indent).append("  Index: null\n");
        }
        return builder.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.ARRAY_ACCESS;
    }
}
