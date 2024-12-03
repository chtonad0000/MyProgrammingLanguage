package com.l_st_lxx.Nodes;

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
}
