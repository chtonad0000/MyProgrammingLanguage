package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class UnaryOperationNode extends ASTNode {
    private final ASTNode operand;
    private final String operator;

    public UnaryOperationNode(ASTNode operand, String operator) {
        this.operand = operand;
        this.operator = operator;
    }

    public ASTNode getOperand() {
        return operand;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void print() {
        System.out.print(operator);
        operand.print();
    }

    @Override
    public String toPrettyString(int indentLevel) {
        return getIndent(indentLevel) + operator +
                operand.toPrettyString(indentLevel + 1);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.UNARY_OPERATION;
    }
}
