package com.Chyb.lexer;

import com.Chyb.lexer.interfaces.ILexer;
import com.Chyb.token.Token;
import com.Chyb.token.interfaces.IToken;
import com.Chyb.token.type.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Lexer implements ILexer {
    private final String input;
    private int position = 0;
    public Lexer(String input) {
        this.input = input;
    }
    private IToken tokenize (){
        String regex = "^'.{1}'$";
        Pattern pattern = Pattern.compile(regex);
        if (position + 3 < input.length()) {
            if (pattern.matcher(input.substring(position, position+3)).matches()) {
                position += 3;
                return new Token(input.substring(position-3, position), TokenType.CHAR_LITERAL);
            }
        }
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

        if (input.startsWith("int", position)) {
            if (!Character.isLetter(input.charAt(position+3)) && !Character.isDigit(input.charAt(position+3))) {
                position += 3;
                return new Token("int", TokenType.TYPE);
            }
        }
        if (input.startsWith("char", position)) {
            if (!Character.isLetter(input.charAt(position+4)) && !Character.isDigit(input.charAt(position+4))) {
                position += 4;
                return new Token("char", TokenType.TYPE);
            }
        }
        if (input.startsWith("true", position)) {
            if (!Character.isLetter(input.charAt(position+4)) && !Character.isDigit(input.charAt(position+4))) {
                position+=4;
                return new Token("true", TokenType.BOOLEAN_LITERAL);
            }
        }
        if (input.startsWith("false", position)) {
            if (!Character.isLetter(input.charAt(position+5)) && !Character.isDigit(input.charAt(position+5))) {
                position+=5;
                return new Token("false", TokenType.BOOLEAN_LITERAL);
            }
        }

        if (input.startsWith("bool", position)) {
            if (!Character.isLetter(input.charAt(position+4)) && !Character.isDigit(input.charAt(position+4))) {
                position += 4;
                return new Token("bool", TokenType.TYPE);
            }
        }
        if (input.startsWith("array", position)) {
            if (!Character.isLetter(input.charAt(position+5)) && !Character.isDigit(input.charAt(position+5))) {
                position += 5;
                return new Token("array", TokenType.TYPE);
            }
        }
        if (input.startsWith("void", position)) {
            if (!Character.isLetter(input.charAt(position+4)) && !Character.isDigit(input.charAt(position+4))) {
                position += 4;
                return new Token("void", TokenType.VOID);
            }
        }
        if (input.startsWith("return", position)) {
            if (!Character.isLetter(input.charAt(position+6))  && !Character.isDigit(input.charAt(position+6))) {
                position += 6;
                return new Token("return", TokenType.RETURN);
            }
        }
        if (input.startsWith("if", position)) {
            if (!Character.isLetter(input.charAt(position+2)) && !Character.isDigit(input.charAt(position+2))) {
                position += 2;
                return new Token("if", TokenType.IF);
            }
        }
        if (input.startsWith("else", position)) {
            if (!Character.isLetter(input.charAt(position+4)) && !Character.isDigit(input.charAt(position+4))) {
                position += 4;
                return new Token("else", TokenType.ELSE);
            }
        }
        if (input.startsWith("for", position)) {
            if (!Character.isLetter(input.charAt(position+3)) && !Character.isDigit(input.charAt(position+3))) {
                position += 3;
                return new Token("for", TokenType.FOR);
            }
        }
        if (input.startsWith("while", position)) {
            if (!Character.isLetter(input.charAt(position+5)) && !Character.isDigit(input.charAt(position+5))) {
                position += 5;
                return new Token("while", TokenType.WHILE);
            }
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
            if (!Character.isLetter(input.charAt(position+3)) && !Character.isDigit(input.charAt(position+3))) {
                position += 3;
                return new Token("New", TokenType.FUNCTION);
            }
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
        if (Character.isLetter(input.charAt(position))) {
            StringBuilder name = new StringBuilder();
            while (Character.isDigit(input.charAt(position)) || Character.isLetter(input.charAt(position))) {
                name.append(input.charAt(position));
                position++;
            }
            if (input.charAt(position)=='(') {
                return new Token(name.toString(), TokenType.FUNCTION);
            }
            return new Token(name.toString(), TokenType.IDENTIFIER);
        }
        position++;
        return null;
    }

    public List<IToken> getTokens(){
        List<IToken> tokens = new ArrayList<>();
        while (position<input.length()) {
            IToken token = tokenize();
            if (token!=null) {
                tokens.add(token);
            }
        }
        return tokens;
    }
}
