package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

import java.util.List;

public class ProgramNode extends ASTNode {
    private final List<ASTNode> functions;

    public ProgramNode(List<ASTNode> functions) {
        this.functions = functions;
    }

    public List<ASTNode> getFunctions() {
        return functions;
    }

    @Override
    public void print() {
        System.out.println("ProgramNode:");
        for (ASTNode function : functions) {
            function.print();
        }
    }

    @Override
    public String toPrettyString(int indentLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indentLevel)).append("ProgramNode:\n");
        for (ASTNode function : functions) {
            sb.append(function.toPrettyString(indentLevel + 1)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.PROGRAM;
    }
}
