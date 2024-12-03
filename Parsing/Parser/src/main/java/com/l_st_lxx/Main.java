package com.l_st_lxx;

import com.Chyb.lexer.Lexer;
import com.Chyb.token.interfaces.IToken;
import com.Chyb.token.type.TokenType;
import com.l_st_lxx.Nodes.ASTNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "D:\\PROJECTS\\MyProgrammingLanguage\\Parsing\\Parser\\src\\main\\java\\com\\l_st_lxx\\source.txt"; // Укажите путь к вашему файлу
        String content = readFile(fileName);

        Lexer lexer = new Lexer(content);
        List<IToken> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);

        try {
            ASTNode ast = parser.Parse();
            System.out.println("Parsing successful: " + ast);
            ast.print();
        } catch (RuntimeException e) {
            System.out.println("Parsing failed: " + e.getMessage());
        }
    }

    public static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    static class Token implements IToken {
        private final TokenType type;
        private final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public TokenType getType() {
            return type;
        }
    }
}
