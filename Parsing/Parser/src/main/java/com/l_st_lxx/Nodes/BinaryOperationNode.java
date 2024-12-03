package com.l_st_lxx.Nodes;

public class BinaryOperationNode extends ASTNode {
    private final ASTNode left;
    private final ASTNode right;
    private final String operator;

    public BinaryOperationNode(ASTNode left, ASTNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        return indent + "BinaryOperationNode {\n" +
                indent + "  Operator: \"" + operator + "\",\n" +
                indent + "  Left: " + (left != null ? left.toPrettyString(indentLevel + 1) : "null") + ",\n" +
                indent + "  Right: " + (right != null ? right.toPrettyString(indentLevel + 1) : "null") + "\n" +
                indent + "}";
    }
}
