package com.l_st_lxx.Nodes;

import com.l_st_lxx.Visitor.ASTVisitor;

public class ArrayDeclarationNode extends ASTNode {
    private final String elementType;
    private final String name;

    public ArrayDeclarationNode(String elementType, String name) {
        this.elementType = elementType;
        this.name = name;
    }

    public String getElementType() {
        return elementType;
    }

    public String getName() {
        return name;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        return indent + "ArrayDeclarationNode {\n" +
                indent + "  Element Type: " + elementType + ",\n" +
                indent + "  Name: " + name + "\n" +
                indent + "}";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.ARRAY_DECLARATION;
    }
}
