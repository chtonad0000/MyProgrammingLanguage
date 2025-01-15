package com.l_st_lxx.Visitor;

import com.l_st_lxx.Nodes.*;

public interface ASTVisitor {
    void visit(ArgumentsNode node);
    void visit(ArrayAccessNode node);
    void visit(AssignmentNode node);
    void visit(ArrayDeclarationNode node);
    void visit(ArrayCreationNode node);
    void visit(BinaryOperationNode node);
    void visit(BlockNode node);
    void visit(BoolNode node);
    void visit(CharNode node);
    void visit(ForNode node);
    void visit(FunctionCallNode node);
    void visit(FunctionDeclarationNode node);
    void visit(IdentifierNode node);
    void visit(IfNode node);
    void visit(NumberNode node);
    void visit(ProgramNode node);
    void visit(UnaryOperationNode node);
    void visit(VariableDeclarationNode node);
    void visit(WhileNode node);
}
