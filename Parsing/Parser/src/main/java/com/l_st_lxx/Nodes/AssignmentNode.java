package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class AssignmentNode extends ASTNode {
    private final ASTNode left;
    private final ASTNode right;

    public AssignmentNode(ASTNode left, ASTNode right) {
        this.left = left;
        this.right = right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        return indent + "AssignmentNode {\n" +
                indent + "  Left: " + left.getNodeType() + (left != null ? left.toPrettyString(indentLevel + 1) : "null") + ",\n" +
                indent + "  Right: " + right.getNodeType() + (right != null ? right.toPrettyString(indentLevel + 1) : "null") + "\n" +
                indent + "}";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.ASSIGNMENT;
    }
}
