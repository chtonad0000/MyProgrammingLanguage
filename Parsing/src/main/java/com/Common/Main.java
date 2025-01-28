package com.Common;

import com.Chub.Visitor.ByteCodeGeneratorVisitor;
import com.Chyb.lexer.Lexer;
import com.Chyb.token.interfaces.IToken;
import com.l_st_lxx.Nodes.ASTNode;
import com.l_st_lxx.Nodes.ProgramNode;
import com.l_st_lxx.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.Common.Main <input_file>");
            return;
        }

        String inputFile = args[0];
        processFile(inputFile);
    }

    public static void processFile(String inputFile) {
        String content = readFile(inputFile);

        if (content.isEmpty()) {
            System.out.println("Input file is empty or could not be read: " + inputFile);
            return;
        }

        Lexer lexer = new Lexer(content);
        List<IToken> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);
        try {
            ASTNode ast = parser.Parse();
            ByteCodeGeneratorVisitor visitor = new ByteCodeGeneratorVisitor();
            visitor.visit((ProgramNode) ast);

            String bytecode = visitor.getBytecode();
            System.out.println(bytecode);
        } catch (RuntimeException e) {
            System.out.println("Parsing failed for file: " + inputFile);
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Failed to read file: " + fileName);
            e.printStackTrace();
        }
        return content.toString();
    }
}
