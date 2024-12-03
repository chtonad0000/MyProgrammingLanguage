package com.l_st_lxx.Nodes;

public class ForNode extends ASTNode {
    private final ASTNode initialization;
    private final ASTNode condition;
    private final ASTNode step;
    private final ASTNode body;

    public ForNode(ASTNode initialization, ASTNode condition, ASTNode step, ASTNode body) {
        this.initialization = initialization;
        this.condition = condition;
        this.step = step;
        this.body = body;
    }

    public ASTNode getInitialization() {
        return initialization;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getStep() {
        return step;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public void print() {
        System.out.print("for (");
        if (initialization != null) initialization.print();
        System.out.print("; ");
        if (condition != null) condition.print();
        System.out.print("; ");
        if (step != null) step.print();
        System.out.println(") ");
        body.print();
    }

    @Override
    public String toPrettyString(int indentLevel) {
        String indent = getIndent(indentLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("for (");
        if (initialization != null) sb.append(initialization.toPrettyString(indentLevel));
        sb.append("; ");
        if (condition != null) sb.append(condition.toPrettyString(indentLevel));
        sb.append("; ");
        if (step != null) sb.append(step.toPrettyString(indentLevel));
        sb.append(") ").append("\n").append(body.toPrettyString(indentLevel + 1));
        return sb.toString();
    }
}
