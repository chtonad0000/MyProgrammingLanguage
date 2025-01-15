package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class ArrayCreationNode extends ASTNode {
    private final ASTNode sizeExpression;

    public ArrayCreationNode(ASTNode sizeExpression) {
        this.sizeExpression = sizeExpression;
    }

    public ASTNode getSizeExpression() {
        return sizeExpression;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append("ArrayCreationNode:\n");
        if (sizeExpression != null) {
            builder.append(indent).append("  Size Expression:\n")
                    .append(sizeExpression.toPrettyString(indentLevel + 2));
        } else {
            builder.append(indent).append("  Size Expression: null\n");
        }
        return builder.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.ARRAY_CREATION;
    }
}
