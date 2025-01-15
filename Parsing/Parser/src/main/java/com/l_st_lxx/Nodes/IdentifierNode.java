package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class IdentifierNode extends ASTNode {
    private final String identifier;

    public IdentifierNode(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void print() {
        System.out.print(identifier);
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        return indent + identifier + "\n";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.IDENTIFIER;
    }
}
