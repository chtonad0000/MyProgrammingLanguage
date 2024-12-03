package com.l_st_lxx.Nodes;

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
                indent + "  Left: " + (left != null ? left.toPrettyString(indentLevel + 1) : "null") + ",\n" +
                indent + "  Right: " + (right != null ? right.toPrettyString(indentLevel + 1) : "null") + "\n" +
                indent + "}";
    }
}
