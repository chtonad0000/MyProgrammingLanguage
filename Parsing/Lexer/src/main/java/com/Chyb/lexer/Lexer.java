package com.Chyb.lexer;

import com.Chyb.lexer.interfaces.ILexer;
import com.Chyb.token.Token;
import com.Chyb.token.interfaces.IToken;
import com.Chyb.token.type.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Lexer implements ILexer {
    private final String input;
    private int position = 0;
    public Lexer(String input) {
        this.input = input;
    }
    public IToken tokenize (){
        if (position>=input.length()) return null;
        if (input.startsWith("+", position)) {
            position++;
            return new Token("+", TokenType.OPERATOR);
        }
        if (input.startsWith("-", position)) {
            position++;
            return new Token("-", TokenType.OPERATOR);
        }
        if (input.startsWith("*", position)) {
            position++;
            return new Token("*", TokenType.OPERATOR);
        }
        if (input.startsWith("/", position)) {
            position++;
            return new Token("/", TokenType.OPERATOR);
        }
        if (input.startsWith(">= ", position)) {
            position+=2;
            return new Token(">=", TokenType.OPERATOR);
        }
        if (input.startsWith("<= ", position)) {
            position+=2;
            return new Token("<=", TokenType.OPERATOR);
        }
        if (input.startsWith("< ", position)) {
            position++;
            return new Token("<", TokenType.OPERATOR);
        }
        if (input.startsWith("> ", position)) {
            position++;
            return new Token(">", TokenType.OPERATOR);
        }
        if (input.startsWith("== ", position)) {
            position+=2;
            return new Token("==", TokenType.OPERATOR);
        }
        if (input.startsWith("!= ", position)) {
            position+=2;
            return new Token("!=", TokenType.OPERATOR);
        }
        if (input.startsWith("|| ", position)) {
            position+=2;
            return new Token("||", TokenType.OPERATOR);
        }
        if (input.startsWith("&& ", position)) {
            position+=2;
            return new Token("&&", TokenType.OPERATOR);
        }

        if (input.startsWith("int ", position)) {
            position+=3;
            return new Token("int", TokenType.TYPE);
        }
        if (input.startsWith("char ", position)) {
            position+=4;
            return new Token("char", TokenType.TYPE);
        }
        if (input.startsWith("bool ", position)) {
            position+=4;
            return new Token("bool", TokenType.TYPE);
        }
        if (input.startsWith("array ", position)) {
            position+=5;
            return new Token("array", TokenType.TYPE);
        }
        if (input.startsWith("void ", position)) {
            position+=4;
            return new Token("void", TokenType.VOID);
        }
        if (input.startsWith("return", position)) {
            position+=6;
            return new Token("return ", TokenType.RETURN);
        }
        if (input.startsWith("if ", position)) {
            position+=2;
            return new Token("if", TokenType.IF);
        }
        if (input.startsWith("else ", position)) {
            position+=4;
            return new Token("else", TokenType.ELSE);
        }
        if (input.startsWith("for ", position)) {
            position+=3;
            return new Token("for", TokenType.FOR);
        }
        if (input.startsWith("while ", position)) {
            position+=5;
            return new Token("while", TokenType.WHILE);
        }
        if (input.startsWith("= ", position)) {
            position+=1;
            return new Token("=", TokenType.ASSIGNMENT);
        }
        if (input.startsWith(", ", position)) {
            position+=1;
            return new Token(",", TokenType.COMMA);
        }
        if (input.startsWith(".", position)) {
            position+=1;
            return new Token(".", TokenType.DOT);
        }
        if (input.startsWith("New", position)) {
            position+=3;
            return new Token("New", TokenType.FUNCTION);
        }
        if (input.startsWith(";", position)) {
            position+=1;
            return new Token(";", TokenType.SEMICOLON);
        }
        if (input.startsWith("(", position)) {
            position+=1;
            return new Token("(", TokenType.LPAREN);
        }
        if (input.startsWith(")", position)) {
            position+=1;
            return new Token(")", TokenType.RPAREN);
        }
        if (input.startsWith("{", position)) {
            position+=1;
            return new Token("{", TokenType.LBRACE);
        }
        if (input.startsWith("}", position)) {
            position+=1;
            return new Token("}", TokenType.RBRACE);
        }
        if (input.startsWith("[", position)) {
            position+=1;
            return new Token("[", TokenType.LBRACKET);
        }
        if (input.startsWith("]", position)) {
            position+=1;
            return new Token("]", TokenType.RBRACKET);
        }
        if (Character.isDigit(input.charAt(position))) {
            StringBuilder number = new StringBuilder();
            while (Character.isDigit(input.charAt(position))) {
                number.append(input.charAt(position));
                position++;
            }
            return new Token(number.toString(), TokenType.NUMBER);
        }
        if (Character.isDigit(input.charAt(position))) {
            StringBuilder name = new StringBuilder();
            while (Character.isLetter(input.charAt(position))) {
                name.append(input.charAt(position));
                position++;
            }
            return new Token(name.toString(), TokenType.IDENTIFIER);
        }
        position++;
        return new Token("null", TokenType.INVALID);
    }

    public List<IToken> getTokens(){
        List<IToken> tokens = new ArrayList<>();
        while (position<input.length()) {
            IToken token = tokenize();
            tokens.add(token);
        }
        return tokens;
    }
}
