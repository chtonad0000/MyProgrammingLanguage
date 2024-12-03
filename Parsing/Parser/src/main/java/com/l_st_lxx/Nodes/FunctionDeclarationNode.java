package com.l_st_lxx.Nodes;

import java.util.List;

public class FunctionDeclarationNode extends ASTNode {
    private final String returnType;
    private final String name;
    private final List<VariableDeclarationNode> parameters;
    private final ASTNode body;

    public FunctionDeclarationNode(String returnType, String name, List<VariableDeclarationNode> parameters, ASTNode body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<VariableDeclarationNode> getParameters() {
        return parameters;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "FunctionDeclarationNode(" +
                "returnType='" + returnType + '\'' +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                ", body=" + body +
                ')';
    }

    @Override
    public void print() {
        System.out.print(returnType + " " + name + "(");
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).print();
            if (i < parameters.size() - 1) System.out.print(", ");
        }
        System.out.print(") ");
        body.print();
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(returnType).append(" ").append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).toPrettyString(indentLevel));
            if (i < parameters.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")\n");
        sb.append(body.toPrettyString(indentLevel));
        return sb.toString();
    }
}
