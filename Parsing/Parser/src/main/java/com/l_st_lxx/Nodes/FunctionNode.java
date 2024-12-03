package com.l_st_lxx.Nodes;

public class FunctionNode extends ASTNode {
    private final String functionName;
    private final ASTNode arguments;

    public FunctionNode(String functionName, ASTNode arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ASTNode getArguments() {
        return arguments;
    }

    @Override
    public void print() {
        System.out.print("FunctionNode(" + functionName + ", ");
        if (arguments != null) {
            arguments.print();
        } else {
            System.out.print("No Arguments");
        }
        System.out.print(")");
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(functionName).append("(");
        if (arguments != null) {
            sb.append(arguments.toPrettyString(indentLevel));
        } else {
            sb.append("No Arguments");
        }
        sb.append(")\n");
        return sb.toString();
    }
}
