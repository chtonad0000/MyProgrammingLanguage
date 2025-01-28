package com.Chub.Visitor;

import com.l_st_lxx.Nodes.*;
import com.l_st_lxx.Visitor.ASTVisitor;

public class ByteCodeGeneratorVisitor implements ASTVisitor {
    private final StringBuilder bytecode = new StringBuilder();
    private int labelCounter = 0;

    public String getBytecode() {
        return bytecode.toString();
    }

    @Override
    public void visit(ArgumentsNode node) {
    }

    @Override
    public void visit(ArrayAccessNode node) {
        node.getIndex().accept(this);
        bytecode.append("LOAD_ARRAY ").append(node.getArrayName()).append("\n");
    }

    @Override
    public void visit(AssignmentNode node) {
        ASTNode left = node.getLeft();
        ASTNode right = node.getRight();
        if (left instanceof IdentifierNode varNode && right instanceof ArrayCreationNode arrCreate) {
            arrCreate.getSizeExpression().accept(this);
            bytecode.append("ALLOCATE_ARRAY ").append(varNode.getIdentifier()).append("\n");
        } else if (left instanceof IdentifierNode varNode) {
            right.accept(this);
            bytecode.append("STORE_VAR ").append(varNode.getIdentifier()).append("\n");
        } else if (left instanceof ArrayAccessNode arrayNode) {
            right.accept(this);
            arrayNode.getIndex().accept(this);
            bytecode.append("STORE_ARRAY ").append(arrayNode.getArrayName()).append("\n");
        } else {
            throw new RuntimeException("AssignmentNode left must be var or arrayAccess");
        }
    }

    @Override
    public void visit(ArrayDeclarationNode node) {
        bytecode.append("DECLARE_ARRAY ").append(node.getElementType()).append(" ").append(node.getName()).append("\n");
    }

    @Override
    public void visit(ArrayCreationNode node) {
        node.getSizeExpression().accept(this);
        bytecode.append("ALLOCATE_ARRAY\n");
    }

    @Override
    public void visit(BinaryOperationNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        String op = node.getOperator();
        switch (op) {
            case "+" -> bytecode.append("ADD\n");
            case "-" -> bytecode.append("SUB\n");
            case "*" -> bytecode.append("MUL\n");
            case "/" -> bytecode.append("DIV\n");
            case "%" -> bytecode.append("MOD\n");
            case "==" -> bytecode.append("EQ\n");
            case "!=" -> bytecode.append("NEQ\n");
            case "<" -> bytecode.append("LT\n");
            case ">" -> bytecode.append("GT\n");
            case "<=" -> bytecode.append("LEQ\n");
            case ">=" -> bytecode.append("GEQ\n");
            case "||" -> bytecode.append("OR\n");
            case "&&" -> bytecode.append("AND\n");
            default -> throw new RuntimeException("Unsupported operator: " + op);
        }
    }

    @Override
    public void visit(BlockNode node) {
        for (ASTNode st : node.getStatements()) {
            st.accept(this);
        }
    }

    @Override
    public void visit(BoolNode node) {
        bytecode.append("LOAD_CONST ").append(node.getValue() ? "true" : "false").append("\n");
    }

    @Override
    public void visit(CharNode node) {
        bytecode.append("LOAD_CONST '").append(node.getValue()).append("'\n");
    }

    @Override
    public void visit(ForNode node) {
        String startLabel = "label_" + (labelCounter++);
        String endLabel = "label_" + (labelCounter++);
        if (node.getInitialization() != null) {
            node.getInitialization().accept(this);
        }
        bytecode.append("LABEL ").append(startLabel).append("\n");
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
            bytecode.append("JUMP_IF_FALSE ").append(endLabel).append("\n");
        }
        node.getBody().accept(this);
        if (node.getStep() != null) {
            node.getStep().accept(this);
        }
        bytecode.append("JUMP ").append(startLabel).append("\n");
        bytecode.append("LABEL ").append(endLabel).append("\n");
    }

    @Override
    public void visit(FunctionCallNode node) {
        String funcName = node.getFunctionName();
        for (ASTNode arg : node.getArguments()) {
            arg.accept(this);
        }
        if (funcName.equals("print")) {
            bytecode.append("PRINT\n");
        } else {
            bytecode.append("CALL ").append(funcName).append("\n");
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        bytecode.append("FUNC_BEGIN ").append(node.getName()).append("\n");
        for (VariableDeclarationNode param : node.getParameters()) {
            bytecode.append("DECLARE_VAR ").append(param.getType()).append(" ").append(param.getIdentifier()).append("\n");
            bytecode.append("STORE_VAR ").append(param.getIdentifier()).append("\n");
        }
        node.getBody().accept(this);
        bytecode.append("FUNC_END ").append(node.getName()).append("\n");
    }

    @Override
    public void visit(IdentifierNode node) {
        bytecode.append("LOAD_VAR ").append(node.getIdentifier()).append("\n");
    }

    @Override
    public void visit(IfNode node) {
        String labelElse = "label_" + labelCounter++;
        String labelEnd = "label_" + labelCounter++;
        node.getCondition().accept(this);
        bytecode.append("JUMP_IF_FALSE ").append(labelElse).append("\n");
        node.getThenBlock().accept(this);
        bytecode.append("JUMP ").append(labelEnd).append("\n");
        bytecode.append("LABEL ").append(labelElse).append("\n");
        if (node.getElseBlock() != null) {
            node.getElseBlock().accept(this);
        }
        bytecode.append("LABEL ").append(labelEnd).append("\n");
    }

    @Override
    public void visit(NumberNode node) {
        bytecode.append("LOAD_CONST ").append(node.getValue()).append("\n");
    }

    @Override
    public void visit(ProgramNode node) {
        bytecode.append("START_PROGRAM\n");
        for (ASTNode f : node.getFunctions()) {
            f.accept(this);
        }
        bytecode.append("END_PROGRAM\n");
    }

    @Override
    public void visit(UnaryOperationNode node) {
        node.getOperand().accept(this);
        switch (node.getOperator()) {
            case "-" -> bytecode.append("NEG\n");
            case "!" -> bytecode.append("NOT\n");
            default -> throw new RuntimeException("Unsupported unary operator: " + node.getOperator());
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        bytecode.append("DECLARE_VAR ").append(node.getType()).append(" ").append(node.getIdentifier()).append("\n");
    }

    @Override
    public void visit(WhileNode node) {
        String startLabel = "label_" + labelCounter++;
        String endLabel = "label_" + labelCounter++;
        bytecode.append("LABEL ").append(startLabel).append("\n");
        node.getCondition().accept(this);
        bytecode.append("JUMP_IF_FALSE ").append(endLabel).append("\n");
        node.getBody().accept(this);
        bytecode.append("JUMP ").append(startLabel).append("\n");
        bytecode.append("LABEL ").append(endLabel).append("\n");
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        bytecode.append("RETURN\n");
    }
}
