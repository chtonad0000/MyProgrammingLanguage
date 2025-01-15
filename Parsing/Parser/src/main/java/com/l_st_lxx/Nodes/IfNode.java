package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class IfNode extends ASTNode {
    private final ASTNode condition;
    private final ASTNode thenBlock;
    private final ASTNode elseBlock;

    public IfNode(ASTNode condition, ASTNode thenBlock, ASTNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public ASTNode getCondition() { return condition; }
    public ASTNode getThenBlock() { return thenBlock; }
    public ASTNode getElseBlock() { return elseBlock; }

    @Override
    public void print() {
        System.out.print("if (");
        condition.print();
        System.out.print(") ");
        thenBlock.print();
        if (elseBlock != null) {
            System.out.print(" else ");
            elseBlock.print();
        }
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder result = new StringBuilder();

        result.append(indent).append("if (");
        result.append(condition.toPrettyString(indentLevel + 1));
        result.append(indent).append(") ");
        result.append(thenBlock.toPrettyString(indentLevel + 1));

        if (elseBlock != null) {
            result.append(indent).append("else ");
            result.append(elseBlock.toPrettyString(indentLevel + 1));
        }

        return result.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.IF;
    }
}
