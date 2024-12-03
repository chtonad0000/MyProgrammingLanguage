package com.l_st_lxx.Nodes;

public class WhileNode extends ASTNode {
    private final ASTNode condition;
    private final ASTNode body;

    public WhileNode(ASTNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public void print() {
        System.out.print("while (");
        condition.print();
        System.out.print(") ");
        body.print();
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        return indent + "while (" + condition.toPrettyString(0) + ")\n" +
                body.toPrettyString(indentLevel + 1);
    }
}
