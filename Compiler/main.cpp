#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <stdexcept>
#include <chrono>
#include <cstdio>
#include "Interpreter.h"


std::vector<Instruction> loadBytecode(const std::string& filename) {
    std::vector<Instruction> code;
    std::ifstream fin(filename);
    if (!fin.is_open()) {
        throw std::runtime_error("Cannot open bytecode file: " + filename);
    }

    std::string opstr, arg;
    while (fin >> opstr) {
        std::getline(fin, arg);
        if (!arg.empty() && arg[0] == ' ') {
            arg.erase(0, 1);
        }

        Instruction ins;
        if (opstr == "LOAD_CONST") ins.op = OpCode::LOAD_CONST;
        else if (opstr == "STORE_VAR") ins.op = OpCode::STORE_VAR;
        else if (opstr == "LOAD_VAR") ins.op = OpCode::LOAD_VAR;
        else if (opstr == "ADD") ins.op = OpCode::ADD;
        else if (opstr == "SUB") ins.op = OpCode::SUB;
        else if (opstr == "MUL") ins.op = OpCode::MUL;
        else if (opstr == "DIV") ins.op = OpCode::DIV;
        else if (opstr == "MOD") ins.op = OpCode::MOD;
        else if (opstr == "EQ") ins.op = OpCode::EQ;
        else if (opstr == "LT") ins.op = OpCode::LT;
        else if (opstr == "JUMP") ins.op = OpCode::JUMP;
        else if (opstr == "JUMP_IF_FALSE") ins.op = OpCode::JUMP_IF_FALSE;
        else if (opstr == "LABEL") ins.op = OpCode::LABEL;
        else if (opstr == "CALL") ins.op = OpCode::CALL;
        else if (opstr == "RETURN") ins.op = OpCode::RETURN;
        else if (opstr == "FUNC_BEGIN") ins.op = OpCode::FUNC_BEGIN;
        else if (opstr == "FUNC_END") ins.op = OpCode::FUNC_END;
        else if (opstr == "DECLARE_ARRAY") ins.op = OpCode::DECLARE_ARRAY;
        else if (opstr == "ALLOCATE_ARRAY") ins.op = OpCode::ALLOCATE_ARRAY;
        else if (opstr == "STORE_ARRAY") ins.op = OpCode::STORE_ARRAY;
        else if (opstr == "LOAD_ARRAY") ins.op = OpCode::LOAD_ARRAY;
        else if (opstr == "START_PROGRAM") ins.op = OpCode::START_PROGRAM;
        else if (opstr == "END_PROGRAM") ins.op = OpCode::END_PROGRAM;
        else if (opstr == "DECLARE_VAR") ins.op = OpCode::DECLARE_VAR;
        else if (opstr == "PRINT") ins.op = OpCode::PRINT;
        else if (opstr == "GT") ins.op = OpCode::GT;
        else if (opstr == "LEQ") ins.op = OpCode::LEQ;
        else if (opstr == "GEQ") ins.op = OpCode::GEQ;
        else if (opstr == "AND") ins.op = OpCode::AND;
        else if (opstr == "OR") ins.op = OpCode::OR;
        else {
            std::cout << "Unknown opcode: " << opstr << std::endl;
            ins.op = OpCode::UNKNOWN;
        }

        ins.argument = arg;
        code.push_back(ins);
    }
    return code;
}


void executeProgram(const std::string& bytecodeFile) {
    try {
        auto code = loadBytecode(bytecodeFile);
        Interpreter interpreter(code);
        interpreter.runProgram();
    }
    catch (const std::exception& ex) {
        std::cerr << "Error executing program '" << bytecodeFile << "': " << ex.what() << "\n";
    }
}


void executeProgramFromBytecode(const std::string& bytecode) {
    const std::string tempFile = "temp_bytecode.txt";
    std::ofstream outFile(tempFile);
    if (!outFile.is_open()) {
        throw std::runtime_error("Failed to create temporary bytecode file");
    }
    outFile << bytecode;
    outFile.close();

    executeProgram(tempFile);

    std::remove(tempFile.c_str());
}

std::string readSourceCode(const std::string& filePath) {
    std::ifstream file(filePath);
    if (!file.is_open()) {
        throw std::runtime_error("Failed to open file: " + filePath);
    }
    std::stringstream buffer;
    buffer << file.rdbuf();
    return buffer.str();
}

std::string runJavaProgram(const std::string& sourceCode) {
    std::string buildCommand = "cd D:\\PROJECTS\\MyProgrammingLanguage\\Parsing && gradlew build -q";

    int buildResult = std::system(buildCommand.c_str());
    if (buildResult != 0) {
        throw std::runtime_error("Java build failed with exit code: " + std::to_string(buildResult));
    }

    std::string javaClasspath =
        "D:\\PROJECTS\\MyProgrammingLanguage\\Parsing\\build\\classes\\java\\main;"
        "D:\\PROJECTS\\MyProgrammingLanguage\\Parsing\\Lexer\\build\\classes\\java\\main;"
        "D:\\PROJECTS\\MyProgrammingLanguage\\Parsing\\ByteCodeGenerator\\build\\classes\\java\\main;"
        "D:\\PROJECTS\\MyProgrammingLanguage\\Parsing\\Parser\\build\\classes\\java\\main;"
        "D:\\PROJECTS\\MyProgrammingLanguage\\Parsing\\Token\\build\\classes\\java\\main";

    std::string tempSourceFile = "temp_source.txt";
    std::ofstream outFile(tempSourceFile);
    if (!outFile.is_open()) {
        throw std::runtime_error("Failed to create temporary source file");
    }
    outFile << sourceCode;
    outFile.close();

    std::string command = "java -cp \"" + javaClasspath + "\" com.Common.Main " + tempSourceFile;
    std::ostringstream javaOutput;
    FILE* pipe = _popen(command.c_str(), "r");
    if (!pipe) {
        throw std::runtime_error("Failed to run Java program");
    }
    char buffer[128];
    while (fgets(buffer, sizeof(buffer), pipe) != nullptr) {
        javaOutput << buffer;
    }
    _pclose(pipe);

    std::remove(tempSourceFile.c_str());
    return javaOutput.str();
}

int main(int argc, char** argv) {
    if (argc < 2) {
        std::cerr << "Usage: " << argv[0] << " <file>" << std::endl;
        return 1;
    }

    try {
        std::string file = argv[1];
        std::string sourceCode = readSourceCode(file);

        std::string bytecode = runJavaProgram(sourceCode);

        executeProgramFromBytecode(bytecode);
    } catch (const std::exception& ex) {
        std::cerr << "Error: " << ex.what() << std::endl;
        return 1;
    }
    return 0;
}
