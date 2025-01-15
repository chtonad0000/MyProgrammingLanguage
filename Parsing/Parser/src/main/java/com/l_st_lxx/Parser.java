package com.l_st_lxx;

import com.Chyb.token.interfaces.IToken;
import com.Chyb.token.type.TokenType;
import com.l_st_lxx.Nodes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Parser {
    private final Iterator<IToken> iterator;
    private IToken currentToken;

    private IToken currentToken() {
        return currentToken;
    }

    public Parser(Collection<IToken> tokens) {
        iterator = tokens.iterator();
        if (iterator.hasNext()) {
            currentToken = iterator.next();
        }
    }

    public ASTNode Parse() {
        List<ASTNode> funcNodes = new ArrayList<>();
        while (currentToken != null) {
            funcNodes.add(parseFunctionDeclaration());
        }

        return new ProgramNode(funcNodes);
    }


    public void moveToNext() {
        if (iterator.hasNext()) {
            currentToken = iterator.next();
        } else {
            currentToken = null;
        }
    }

    private boolean isType(TokenType type) {
        return currentToken() != null && currentToken().getType() == type;
    }

    private void expect(TokenType type) {
        if (!isType(type)) {
            throw new RuntimeException("Expected token of type " + type + ", but got " + currentToken().getValue() + " " +
                    (currentToken() != null ? currentToken().getType() : "EOF"));
        }
        moveToNext();
    }

    private ASTNode parseFunctionDeclaration() {
        if (!isType(TokenType.TYPE)) {
            throw new RuntimeException("Expected return type in function declaration.");
        }
        String returnType = currentToken().getValue();
        moveToNext();

        if (!isType(TokenType.FUNCTION)) {
            throw new RuntimeException("Expected function name in function declaration.");
        }
        String functionName = currentToken().getValue();
        moveToNext();

        expect(TokenType.LPAREN);

        List<VariableDeclarationNode> parameters = new ArrayList<>();
        if (!isType(TokenType.RPAREN)) {
            do {
                if (!isType(TokenType.TYPE)) {
                    throw new RuntimeException("Expected parameter type in function declaration.");
                }
                String paramType = currentToken().getValue();
                moveToNext();

                if (!isType(TokenType.IDENTIFIER)) {
                    throw new RuntimeException("Expected parameter name in function declaration.");
                }
                String paramName = currentToken().getValue();
                moveToNext();

                parameters.add(new VariableDeclarationNode(paramType, paramName));
                if (isType(TokenType.COMMA)) {
                    moveToNext();
                }
            } while (!isType(TokenType.RPAREN));
        }

        expect(TokenType.RPAREN);

        ASTNode body = parseBlock();

        return new FunctionDeclarationNode(returnType, functionName, parameters, body);
    }

    private ASTNode parseForStatement() {
        expect(TokenType.FOR);
        expect(TokenType.LPAREN);

        ASTNode initialization = null;
        if (!isType(TokenType.SEMICOLON)) {
            initialization = parseStatement();
        } else {
            expect(TokenType.SEMICOLON);
        }

        ASTNode condition = null;
        if (!isType(TokenType.SEMICOLON)) {
            condition = parseExpression();
        }
        expect(TokenType.SEMICOLON);

        ASTNode step = null;
        if (!isType(TokenType.RPAREN)) {
            step = parseStatement();
        }
        expect(TokenType.RPAREN);

        ASTNode body = parseBlock();

        return new ForNode(initialization, condition, step, body);
    }


    private ASTNode parseIfStatement() {
        expect(TokenType.IF);
        expect(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        expect(TokenType.RPAREN);
        ASTNode thenBlock = parseBlock();
        ASTNode elseBlock = null;
        if (isType(TokenType.ELSE)) {
            moveToNext();
            elseBlock = parseBlock();
        }
        return new IfNode(condition, thenBlock, elseBlock);
    }

    private ASTNode parseBlock() {
        expect(TokenType.LBRACE);
        BlockNode block = new BlockNode();
        while (currentToken() != null && !isType(TokenType.RBRACE)) {
            block.addStatement(parseStatement());
        }
        expect(TokenType.RBRACE);
        return block;
    }

    private ASTNode parseWhileStatement() {
        expect(TokenType.WHILE);
        expect(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        expect(TokenType.RPAREN);
        ASTNode body = parseBlock();
        return new WhileNode(condition, body);
    }

    private ASTNode parseStatement() {
        if (isType(TokenType.TYPE)) {
            String type = currentToken().getValue();
            moveToNext();

            String elementType = null;
            if (type.equals("array")) {
                if (!isType(TokenType.TYPE)) {
                    throw new RuntimeException("Expected element type after 'array', but got: " +
                            (currentToken() != null ? currentToken().getValue() : "EOF"));
                }
                elementType = currentToken().getValue();
                moveToNext();
            }

            if (!isType(TokenType.IDENTIFIER)) {
                throw new RuntimeException("Expected identifier after type declaration, but got: " +
                        (currentToken() != null ? currentToken().getValue() : "EOF"));
            }

            String identifier = currentToken().getValue();
            moveToNext();
            expect(TokenType.SEMICOLON);

            if (type.equals("array")) {
                return new ArrayDeclarationNode(elementType, identifier);
            } else {
                return new VariableDeclarationNode(type, identifier);
            }
        } else if (isType(TokenType.IDENTIFIER)) {
            String identifier = currentToken().getValue();
            moveToNext();

            ASTNode target;
            if (isType(TokenType.LBRACKET)) {
                moveToNext();
                ASTNode index = parseExpression();
                expect(TokenType.RBRACKET);
                target = new ArrayAccessNode(identifier, index);
            } else {
                target = new IdentifierNode(identifier);
            }

            expect(TokenType.ASSIGNMENT);
            ASTNode value = parseExpression();
            expect(TokenType.SEMICOLON);
            return new AssignmentNode(target, value);
        } else if (isType(TokenType.FUNCTION)) {
            String function = currentToken().getValue();
            moveToNext();
            ASTNode arguments = parseArgs();
            expect(TokenType.SEMICOLON);
            return new FunctionCallNode(function, ((ArgumentsNode)arguments).getArguments());
        } else if (isType(TokenType.IF)) {
            return parseIfStatement();
        } else if (isType(TokenType.WHILE)) {
            return parseWhileStatement();
        } else if (isType(TokenType.FOR)) {
            return parseForStatement();
        }

        throw new RuntimeException("Invalid statement starting at: " +
                (currentToken() != null ? currentToken().getValue() : "EOF"));
    }

    private ASTNode parseArgs() {
        expect(TokenType.LPAREN);

        List<ASTNode> args = new ArrayList<>();

        if (!isType(TokenType.RPAREN)) {
            do {
                args.add(parseExpression());
                if (isType(TokenType.COMMA)) {
                    moveToNext();
                }
            } while (!isType(TokenType.RPAREN));
        }

        expect(TokenType.RPAREN);

        return new ArgumentsNode(args);
    }


    private ASTNode parseExpression() {
        return parseLogicalOr();
    }

    private ASTNode parseLogicalOr() {
        ASTNode left = parseLogicalAnd();

        while (currentToken() != null && isType(TokenType.OPERATOR) &&
                currentToken().getValue().equals("||")) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode right = parseLogicalAnd();
            left = new BinaryOperationNode(left, right, operator);
        }
        return left;
    }

    private ASTNode parseLogicalAnd() {
        ASTNode left = parseEquality();

        while (currentToken() != null && isType(TokenType.OPERATOR) &&
                currentToken().getValue().equals("&&")) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode right = parseEquality();
            left = new BinaryOperationNode(left, right, operator);
        }

        return left;
    }

    private ASTNode parseEquality() {
        ASTNode left = parseRelational();

        while (currentToken() != null && isType(TokenType.OPERATOR) &&
                (currentToken().getValue().equals("==") || currentToken().getValue().equals("!="))) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode right = parseRelational();
            left = new BinaryOperationNode(left, right, operator);
        }

        return left;
    }

    private ASTNode parseRelational() {
        ASTNode left = parseTerm();

        while (currentToken() != null && isType(TokenType.OPERATOR) &&
                (currentToken().getValue().equals("<") || currentToken().getValue().equals(">") ||
                        currentToken().getValue().equals("<=") || currentToken().getValue().equals(">="))) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode right = parseTerm();
            left = new BinaryOperationNode(left, right, operator);
        }
        return left;
    }

    private ASTNode parseTerm() {
        ASTNode left = parseFactor();

        while (currentToken() != null && isType(TokenType.OPERATOR) &&
                (currentToken().getValue().equals("+") || currentToken().getValue().equals("-"))) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode right = parseFactor();
            left = new BinaryOperationNode(left, right, operator);
        }

        return left;
    }

    private ASTNode parseFactor() {
        ASTNode left = parseUnary();

        while (currentToken() != null && isType(TokenType.OPERATOR) &&
                (currentToken().getValue().equals("*") || currentToken().getValue().equals("/"))) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode right = parseUnary();
            left = new BinaryOperationNode(left, right, operator);
        }

        return left;
    }

    private ASTNode parseUnary() {
        if (currentToken() != null && isType(TokenType.OPERATOR) &&
                (currentToken().getValue().equals("-") || currentToken().getValue().equals("!"))) {
            String operator = currentToken().getValue();
            moveToNext();
            ASTNode operand = parsePrimary();
            return new UnaryOperationNode(operand, operator);
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        if (isType(TokenType.NUMBER)) {
            ASTNode numberNode = new NumberNode(Integer.parseInt(currentToken().getValue()));
            moveToNext();
            return numberNode;
        } else if (isType(TokenType.IDENTIFIER)) {
            String identifier = currentToken().getValue();
            moveToNext();

            ASTNode target;
            if (isType(TokenType.LBRACKET)) {
                moveToNext();
                ASTNode index = parseExpression();
                expect(TokenType.RBRACKET);
                target = new ArrayAccessNode(identifier, index);
            } else {
                target = new IdentifierNode(identifier);
            }
            return target;
        } else if (isType(TokenType.CHAR_LITERAL)) {
            ASTNode charNode = new CharNode(currentToken().getValue());
            moveToNext();
            return charNode;
        } else if (isType(TokenType.BOOLEAN_LITERAL)) {
            ASTNode boolNode = new BoolNode(currentToken().getValue());
            moveToNext();
            return boolNode;
        } else if (isType(TokenType.FUNCTION)) {
            String functionName = currentToken().getValue();
            moveToNext();
            expect(TokenType.LPAREN);
            List<ASTNode> args = new ArrayList<>();

            if (!isType(TokenType.RPAREN)) {
                do {
                    args.add(parseExpression());
                    if (isType(TokenType.COMMA)) {
                        moveToNext();
                    }
                } while (!isType(TokenType.RPAREN));
            }
            expect(TokenType.RPAREN);

            return new FunctionCallNode(functionName, args);
        } else if (isType(TokenType.NEW)) {
            moveToNext();

            expect(TokenType.LPAREN);
            ASTNode sizeExpression = parseExpression();
            expect(TokenType.RPAREN);

            return new ArrayCreationNode(sizeExpression);
        } else if (isType(TokenType.LPAREN)) {
            moveToNext();
            ASTNode expr = parseExpression();
            expect(TokenType.RPAREN);
            return expr;
        }

        throw new RuntimeException("unexpected token: " +
                (currentToken() != null ? currentToken().getValue() : "EOF"));
    }
}
