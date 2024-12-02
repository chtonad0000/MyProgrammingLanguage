package com.Chyb.token;

import com.Chyb.token.interfaces.IToken;
import com.Chyb.token.type.TokenType;

public class Token implements IToken {
    private final String value;
    private final TokenType type;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }
}
