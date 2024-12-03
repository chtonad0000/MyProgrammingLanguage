package com.l_st_lxx.Nodes;

import java.util.List;

public class FunctionCallNode extends ASTNode {
    private final String functionName;
    private final List<ASTNode> arguments;

    public FunctionCallNode(String functionName, List<ASTNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public void print() {
        System.out.print(functionName + "(");
        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).print();
            if (i < arguments.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.print(")");
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(functionName).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).toPrettyString(indentLevel));
            if (i < arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}