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
        String arrayName = node.getArrayName();
        ASTNode index = node.getIndex();
        bytecode.append("LOAD_VAR ").append(arrayName).append("\n");
        index.accept(this);
        bytecode.append("ACCESS_ARRAY\n");
    }

    @Override
    public void visit(AssignmentNode node) {
        node.getRight().accept(this);
        if (node.getLeft() instanceof IdentifierNode variableNode) {
            String identifier = variableNode.getIdentifier();
            bytecode.append("STORE_VAR ").append(identifier).append("\n");
        } else if (node.getLeft() instanceof ArrayAccessNode arrayNode) {
            String arrayName = arrayNode.getArrayName();
            ASTNode index = arrayNode.getIndex();
            bytecode.append("LOAD_VAR ").append(arrayName).append("\n");
            index.accept(this);
            bytecode.append("STORE_ARRAY\n");
        } else {
            throw new RuntimeException("Left-hand side of assignment must be a identifier.");
        }
    }

    @Override
    public void visit(ArrayDeclarationNode node) {
        String elementType = node.getElementType();
        String name = node.getName();

        bytecode.append("DECLARE_ARRAY ").append(elementType).append(" ").append(name).append("\n");
    }

    @Override
    public void visit(ArrayCreationNode node) {
        node.getSizeExpression().accept(this);
        bytecode.append("ALLOCATE_ARRAY").append("\n");
    }

    @Override
    public void visit(BinaryOperationNode node) {
        node.getLeft().accept(this);

        node.getRight().accept(this);

        String operator = node.getOperator();

        switch (operator) {
            case "+":
                bytecode.append("ADD\n");
                break;
            case "-":
                bytecode.append("SUB\n");
                break;
            case "*":
                bytecode.append("MUL\n");
                break;
            case "/":
                bytecode.append("DIV\n");
                break;
            case "==":
                bytecode.append("EQ\n");
                break;
            case "!=":
                bytecode.append("NEQ\n");
                break;
            case "<":
                bytecode.append("LT\n");
                break;
            case ">":
                bytecode.append("GT\n");
                break;
            case "<=":
                bytecode.append("LEQ\n");
                break;
            case ">=":
                bytecode.append("GEQ\n");
                break;
            default:
                throw new RuntimeException("Unsupported operator: " + operator);
        }
    }


    @Override
    public void visit(BlockNode node) {
        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(BoolNode node) {
        boolean value = node.getValue();
        bytecode.append("LOAD_CONST '").append(value).append("'\n");
    }

    @Override
    public void visit(CharNode node) {
        char value = node.getValue();
        bytecode.append("LOAD_CONST '").append(value).append("'\n");
    }

    @Override
    public void visit(ForNode node) {
        String startLabel = "label_" + labelCounter++;
        labelCounter++;
        String endLabel = "label_" + labelCounter;
        labelCounter++;
        node.getInitialization().accept(this);
        bytecode.append("LABEL ").append(startLabel).append(":\n");
        node.getCondition().accept(this);
        bytecode.append("IF_FALSE jump_to ").append(endLabel).append("\n");
        node.getBody().accept(this);
        node.getStep().accept(this);
        bytecode.append("jump_to ").append(startLabel).append("\n");
        bytecode.append("LABEL ").append(endLabel).append(":\n");
    }


    @Override
    public void visit(FunctionCallNode node) {
        String functionName = node.getFunctionName();
        for (ASTNode arg : node.getArguments()) {
            arg.accept(this);
        }

        bytecode.append("CALL ").append(functionName).append("\n");
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        String returnType = node.getReturnType();
        String functionName = node.getName();

        bytecode.append("FUNC_BEGIN ").append(functionName).append("\n");

        for (int i = 0; i < node.getParameters().size(); i++) {
            VariableDeclarationNode param = node.getParameters().get(i);
            String paramType = param.getType();
            String paramName = param.getIdentifier();
            bytecode.append("DECLARE_VAR ").append(paramType).append(" ").append(paramName).append("\n");
        }

        node.getBody().accept(this);

        bytecode.append("FUNC_END ").append(functionName).append("\n");
    }

    @Override
    public void visit(IdentifierNode node) {
        String identifier = node.getIdentifier();
        bytecode.append("LOAD_VAR ").append(identifier).append("\n");
    }

    @Override
    public void visit(IfNode node) {
        node.getCondition().accept(this);
        String elseLabel = "label_" + labelCounter++;
        bytecode.append("JUMP_IF_FALSE ").append(elseLabel).append("\n");
        node.getThenBlock().accept(this);
        bytecode.append("JUMP end_if").append("\n");
        bytecode.append("LABEL ").append(elseLabel).append(":").append("\n");
        if (node.getElseBlock() != null) {
            node.getElseBlock().accept(this);
        }
        bytecode.append("LABEL ").append("end_if:").append("\n");
    }

    @Override
    public void visit(NumberNode node) {
        int value = node.getValue();
        bytecode.append("LOAD_CONST ").append(value).append("\n");
    }

    @Override
    public void visit(ProgramNode node) {
        bytecode.append("START_PROGRAM\n");
        for (ASTNode function : node.getFunctions()) {
            function.accept(this);
        }
        bytecode.append("END_PROGRAM\n");
    }

    @Override
    public void visit(UnaryOperationNode node) {
        node.getOperand().accept(this);

        String operator = node.getOperator();

        switch (operator) {
            case "-":
                bytecode.append("NEG\n");
                break;
            case "!":
                bytecode.append("NOT\n");
                break;
            default:
                throw new RuntimeException("Unsupported unary operator: " + operator);
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        String identifier = node.getIdentifier();
        String type = node.getType();

        switch (type) {
            case "int" -> bytecode.append("DECLARE_VAR int ").append(identifier).append("\n");
            case "bool" -> bytecode.append("DECLARE_VAR bool ").append(identifier).append("\n");
            case "char" -> bytecode.append("DECLARE_VAR char ").append(identifier).append("\n");
            default -> throw new RuntimeException("Unsupported variable type: " + type);
        }
    }

    @Override
    public void visit(WhileNode node) {
        String startLabel = "label_" + labelCounter;
        labelCounter++;
        String endLabel = "label_" + labelCounter;
        labelCounter++;
        bytecode.append("LABEL ").append(startLabel).append(":\n");
        node.getCondition().accept(this);
        bytecode.append("IF_FALSE GO_TO ").append(endLabel).append("\n");
        node.getBody().accept(this);
        bytecode.append("GO_TO ").append(startLabel).append("\n");
        bytecode.append("LABEL ").append(endLabel).append(":\n");
    }
}
