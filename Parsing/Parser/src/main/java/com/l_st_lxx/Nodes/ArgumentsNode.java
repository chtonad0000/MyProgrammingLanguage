package com.l_st_lxx.Nodes;

import java.util.List;

public class ArgumentsNode extends ASTNode {
    private final List<ASTNode> arguments;

    public ArgumentsNode(List<ASTNode> arguments) {
        this.arguments = arguments;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toString() {
        return toPrettyString(0);
    }

    public String toPrettyString(int indentLevel) {
        StringBuilder builder = new StringBuilder();
        String indent = "  ".repeat(indentLevel);
        builder.append(indent).append("ArgumentsNode:\n");
        if (arguments != null && !arguments.isEmpty()) {
            for (ASTNode argument : arguments) {
                builder.append(argument.toPrettyString(indentLevel + 1));
            }
        } else {
            builder.append(indent).append("  No Arguments\n");
        }
        return builder.toString();
    }
}
