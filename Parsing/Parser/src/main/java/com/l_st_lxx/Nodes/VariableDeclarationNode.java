package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class VariableDeclarationNode extends ASTNode {
    private final String type;
    private final String identifier;

    public VariableDeclarationNode(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void print() {
        System.out.print(type + " " + identifier);
    }

    @Override
    public String toPrettyString(int indentLevel) {
        return getIndent(indentLevel) + type + " " + identifier;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.VARIABLE_DECLARATION;
    }
}
