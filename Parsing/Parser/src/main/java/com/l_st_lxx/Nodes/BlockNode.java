package com.l_st_lxx.Nodes;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends ASTNode {
    private final List<ASTNode> statements = new ArrayList<>();

    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    @Override
    public void print() {
        System.out.println(toPrettyString(0));
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append("BlockNode {\n");

        if (statements.isEmpty()) {
            builder.append(indent).append("  No statements\n");
        } else {
            for (ASTNode statement : statements) {
                builder.append(statement.toPrettyString(indentLevel + 1)).append("\n");
            }
        }

        builder.append(indent).append("}");
        return builder.toString();
    }
}
