#pragma once

#include <functional>
#include <vector>
#include <unordered_map>
#include "Instruction.h"
#include "Value.h"


struct State {
    std::vector<Value> stack;
    std::unordered_map<std::string, Value> variables;
    std::unordered_map<std::string,int> labelMap;
    std::unordered_map<std::string,int> functionMap;
    std::vector<int> callStack;
    int pc = 0;
    bool running = true;
    long long stepCounter = 0;
    std::unordered_map<int, int> executionCounts;
    std::unordered_map<int, bool> optimizedCommands;
};

class Interpreter {
public:
    explicit Interpreter(const std::vector<Instruction>& code);
    void runProgram();

private:
    State state;
    using CommandFunc = std::function<int(State&)>;
    struct Command {
        CommandFunc execute;
    };
    std::vector<Command> commands;
    const std::vector<Instruction>& code;

    Command makeCommand(int idx);

    void optimizeCommand(int pc);
};
