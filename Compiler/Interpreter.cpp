#include "Instruction.h"
#include "Value.h"
#include "MarkSweep.h"
#include <iostream>
#include <unordered_map>
#include <vector>
#include <functional>
#include <stdexcept>
#include "Interpreter.h"

#include "GCObject.h"

static Value parseConstant(const std::string &arg) {
    if (arg == "true") {
        return Value(true);
    }
    if (arg == "false") {
        return Value(false);
    }
    long long n = std::stoll(arg);
    return Value(n);
}

static void printValue(const Value &val) {
    if (std::holds_alternative<long long>(val)) {
        std::cout << std::get<long long>(val);
    } else if (std::holds_alternative<bool>(val)) {
        bool b = std::get<bool>(val);
        std::cout << (b ? "true" : "false");
    } else {
        GCObject *obj = std::get<GCObject *>(val);
        if (!obj) {
            std::cout << "null";
        } else {
            std::cout << "[";
            for (size_t i = 0; i < obj->arrayData.size(); i++) {
                if (i > 0) std::cout << ", ";
                const Value &elem = obj->arrayData[i];
                if (std::holds_alternative<long long>(elem)) {
                    std::cout << std::get<long long>(elem);
                } else if (std::holds_alternative<bool>(elem)) {
                    std::cout << (std::get<bool>(elem) ? "true" : "false");
                } else {
                    std::cout << "[object]";
                }
            }
            std::cout << "]";
        }
    }
}

static bool eqValues(const Value &va, const Value &vb) {
    if (va.index() != vb.index()) return false;
    switch (va.index()) {
        case 0:
            return std::get<long long>(va) == std::get<long long>(vb);
        case 1:
            return std::get<bool>(va) == std::get<bool>(vb);
        case 2:
            return std::get<GCObject *>(va) == std::get<GCObject *>(vb);
    }
    return false;
}

Interpreter::Interpreter(const std::vector<Instruction> &code): code(code) {
    commands.resize(code.size());
    for (int i = 0; i < (int) code.size(); i++) {
        commands[i] = makeCommand(i);
    }
    for (int i = 0; i < (int) code.size(); i++) {
        const Instruction &ins = code[i];
        if (ins.op == OpCode::LABEL) {
            state.labelMap[ins.argument] = i;
        } else if (ins.op == OpCode::FUNC_BEGIN) {
            state.functionMap[ins.argument] = i;
        }
    }
}

void Interpreter::runProgram() {
    auto it = state.functionMap.find("main");
    if (it == state.functionMap.end()) {
        throw std::runtime_error("No 'main' function found in bytecode!");
    }

    state.pc = it->second + 1;
    state.running = true;

    const int hotThreshold = 100;
    const size_t elementThreshold = 200000;

    while (state.running && state.pc >= 0 && state.pc < (int) commands.size()) {
        state.executionCounts[state.pc]++;

        if (state.executionCounts[state.pc] > hotThreshold && !state.optimizedCommands[state.pc]) {
            optimizeCommand(state.pc);
            state.optimizedCommands[state.pc] = true;
        }

        state.pc = commands[state.pc].execute(state);

        state.stepCounter++;
        if (g_TotalElements > elementThreshold) {
            collectGarbage(state);
        }
    }
    state.variables.clear();
    state.stack.clear();
    state.callStack.clear();
    collectGarbage(state);
}

Interpreter::Command Interpreter::makeCommand(int idx) {
    Command cmd;
    const Instruction &ins = code[idx];

    switch (ins.op) {
        case OpCode::START_PROGRAM:
        case OpCode::END_PROGRAM:
        case OpCode::FUNC_BEGIN:
        case OpCode::FUNC_END:
        case OpCode::LABEL: {
            cmd.execute = [](State &st) {
                return st.pc + 1;
            };
        }
        break;

        case OpCode::DECLARE_VAR: {
            cmd.execute = [argument = ins.argument](State &st) {
                size_t spacePos = argument.find(' ');
                if (spacePos == std::string::npos) throw std::runtime_error("Invalid declaration: " + argument);
                std::string varType = argument.substr(0, spacePos);
                std::string varName = argument.substr(spacePos + 1);
                if (varType == "int") {
                    st.variables[varName] = static_cast<long long>(0);
                } else if (varType == "bool") {
                    st.variables[varName] = false;
                } else {
                    throw std::runtime_error("Unsupported variable type: " + varType);
                }
                return st.pc + 1;
            };
        }
        break;

        case OpCode::DECLARE_ARRAY: {
            std::string arrName = ins.argument;
            cmd.execute = [arrName](State &st) {
                st.variables[arrName] = static_cast<GCObject *>(nullptr);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::ALLOCATE_ARRAY: {
            std::string arrName = ins.argument;
            cmd.execute = [arrName](State &st) {
                if (st.stack.empty()) throw std::runtime_error("ALLOCATE_ARRAY underflow");
                Value vsz = st.stack.back();
                st.stack.pop_back();
                if (!std::holds_alternative<long long>(vsz))
                    throw std::runtime_error("ALLOCATE_ARRAY: size not int");
                long long sz = std::get<long long>(vsz);
                if (sz < 0) throw std::runtime_error("ALLOCATE_ARRAY negative size");
                GCObject *obj = new GCObject();
                obj->resizeArray(sz);
                st.variables[arrName] = obj;
                return st.pc + 1;
            };
        }
        break;

        case OpCode::LOAD_CONST: {
            std::string arg = ins.argument;
            cmd.execute = [arg](State &st) {
                st.stack.push_back(parseConstant(arg));
                return st.pc + 1;
            };
        }
        break;

        case OpCode::STORE_VAR: {
            std::string varName = ins.argument;
            cmd.execute = [varName](State &st) {
                if (st.stack.empty()) throw std::runtime_error("STORE_VAR: underflow");
                Value val = st.stack.back();
                st.stack.pop_back();
                st.variables[varName] = val;
                return st.pc + 1;
            };
        }
        break;

        case OpCode::LOAD_VAR: {
            std::string varName = ins.argument;
            cmd.execute = [varName](State &st) {
                auto itv = st.variables.find(varName);
                if (itv == st.variables.end())
                    throw std::runtime_error("LOAD_VAR: var not found: " + varName);
                st.stack.push_back(itv->second);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::STORE_ARRAY: {
            std::string arrName = ins.argument;
            cmd.execute = [arrName](State &st) {
                if (st.stack.size() < 2) throw std::runtime_error("STORE_ARRAY underflow");
                Value vind = st.stack.back();
                st.stack.pop_back();
                Value vval = st.stack.back();
                st.stack.pop_back();
                auto ita = st.variables.find(arrName);
                if (ita == st.variables.end())
                    throw std::runtime_error("STORE_ARRAY: array not found");
                if (!std::holds_alternative<GCObject *>(ita->second))
                    throw std::runtime_error("STORE_ARRAY: not array");
                GCObject *arr = std::get<GCObject *>(ita->second);
                if (!arr) throw std::runtime_error("STORE_ARRAY: array is null");
                if (!std::holds_alternative<long long>(vind))
                    throw std::runtime_error("STORE_ARRAY: index not int");
                long long idx = std::get<long long>(vind);
                if (idx < 0 || idx >= (long long) arr->arrayData.size())
                    throw std::runtime_error("STORE_ARRAY: out of bounds");
                arr->arrayData[idx] = vval;
                return st.pc + 1;
            };
        }
        break;

        case OpCode::LOAD_ARRAY: {
            std::string arrName = ins.argument;
            cmd.execute = [arrName](State &st) {
                if (st.stack.empty())
                    throw std::runtime_error("LOAD_ARRAY underflow");
                Value vind = st.stack.back();
                st.stack.pop_back();
                auto ita = st.variables.find(arrName);
                if (ita == st.variables.end())
                    throw std::runtime_error("LOAD_ARRAY: array not found");
                if (!std::holds_alternative<GCObject *>(ita->second))
                    throw std::runtime_error("LOAD_ARRAY: not array");
                GCObject *arr = std::get<GCObject *>(ita->second);
                if (!arr)
                    throw std::runtime_error("LOAD_ARRAY: null array");
                if (!std::holds_alternative<long long>(vind))
                    throw std::runtime_error("LOAD_ARRAY: index not int");
                long long idx = std::get<long long>(vind);
                if (idx < 0 || idx >= (long long) arr->arrayData.size())
                    throw std::runtime_error("LOAD_ARRAY: out of bounds");
                st.stack.push_back(arr->arrayData[idx]);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::PRINT: {
            cmd.execute = [](State &st) {
                if (st.stack.empty())
                    throw std::runtime_error("PRINT: underflow");
                Value top = st.stack.back();
                st.stack.pop_back();
                printValue(top);
                std::cout << "\n";
                return st.pc + 1;
            };
        }
        break;

        case OpCode::ADD:
        case OpCode::SUB:
        case OpCode::MUL:
        case OpCode::DIV:
        case OpCode::MOD: {
            OpCode opcode = ins.op;
            cmd.execute = [opcode](State &st) {
                if (st.stack.size() < 2)
                    throw std::runtime_error("Arithmetic: underflow");
                Value vb = st.stack.back();
                st.stack.pop_back();
                Value va = st.stack.back();
                st.stack.pop_back();
                if (!std::holds_alternative<long long>(va) ||
                    !std::holds_alternative<long long>(vb))
                    throw std::runtime_error("Arithmetic: both operands must be int");
                long long a = std::get<long long>(va);
                long long b = std::get<long long>(vb);
                long long r = 0;
                switch (opcode) {
                    case OpCode::ADD: r = a + b;
                        break;
                    case OpCode::SUB: r = a - b;
                        break;
                    case OpCode::MUL: r = a * b;
                        break;
                    case OpCode::DIV:
                        if (b == 0) throw std::runtime_error("Division by zero");
                        r = a / b;
                        break;
                    case OpCode::MOD:
                        if (b == 0) throw std::runtime_error("Modulo by zero");
                        r = a % b;
                        break;
                    default: break;
                }
                st.stack.push_back(r);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::EQ: {
            cmd.execute = [](State &st) {
                if (st.stack.size() < 2)
                    throw std::runtime_error("EQ: underflow");
                Value vb = st.stack.back();
                st.stack.pop_back();
                Value va = st.stack.back();
                st.stack.pop_back();
                bool eq = eqValues(va, vb);
                st.stack.push_back(eq);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::GT:
        case OpCode::LT:
        case OpCode::LEQ:
        case OpCode::GEQ: {
            OpCode opcode = ins.op;
            cmd.execute = [opcode](State &st) {
                if (st.stack.size() < 2)
                    throw std::runtime_error("Comparison: underflow");
                Value vb = st.stack.back();
                st.stack.pop_back();
                Value va = st.stack.back();
                st.stack.pop_back();
                if (!std::holds_alternative<long long>(va) ||
                    !std::holds_alternative<long long>(vb))
                    throw std::runtime_error("Comparison: both must be int");
                long long a = std::get<long long>(va);
                long long b = std::get<long long>(vb);
                bool r = false;
                switch (opcode) {
                    case OpCode::GT: r = (a > b);
                        break;
                    case OpCode::LT: r = (a < b);
                        break;
                    case OpCode::LEQ: r = (a <= b);
                        break;
                    case OpCode::GEQ: r = (a >= b);
                        break;
                    default: break;
                }
                st.stack.push_back(r);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::AND: {
            cmd.execute = [](State &st) {
                if (st.stack.size() < 2)
                    throw std::runtime_error("AND: underflow");
                Value vb = st.stack.back();
                st.stack.pop_back();
                Value va = st.stack.back();
                st.stack.pop_back();
                if (!std::holds_alternative<bool>(va) ||
                    !std::holds_alternative<bool>(vb))
                    throw std::runtime_error("AND: both must be bool");
                bool a = std::get<bool>(va);
                bool b = std::get<bool>(vb);
                st.stack.push_back(a && b);
                return st.pc + 1;
            };
        }
        break;
        case OpCode::OR: {
            cmd.execute = [](State &st) {
                if (st.stack.size() < 2)
                    throw std::runtime_error("OR: underflow");
                Value vb = st.stack.back();
                st.stack.pop_back();
                Value va = st.stack.back();
                st.stack.pop_back();
                if (!std::holds_alternative<bool>(va) ||
                    !std::holds_alternative<bool>(vb))
                    throw std::runtime_error("OR: both must be bool");
                bool a = std::get<bool>(va);
                bool b = std::get<bool>(vb);
                st.stack.push_back(a || b);
                return st.pc + 1;
            };
        }
        break;

        case OpCode::JUMP: {
            std::string labName = ins.argument;
            cmd.execute = [labName](State &st) {
                auto itL = st.labelMap.find(labName);
                if (itL == st.labelMap.end())
                    throw std::runtime_error("JUMP: no label " + labName);
                return itL->second;
            };
        }
        break;

        case OpCode::JUMP_IF_FALSE: {
            std::string labName = ins.argument;
            cmd.execute = [labName](State &st) {
                if (st.stack.empty())
                    throw std::runtime_error("JUMP_IF_FALSE: underflow");
                Value cond = st.stack.back();
                st.stack.pop_back();
                if (!std::holds_alternative<bool>(cond))
                    throw std::runtime_error("JUMP_IF_FALSE: cond not bool");
                bool c = std::get<bool>(cond);
                if (!c) {
                    auto itL = st.labelMap.find(labName);
                    if (itL == st.labelMap.end())
                        throw std::runtime_error("JUMP_IF_FALSE: label not found");
                    return itL->second;
                } else {
                    return st.pc + 1;
                }
            };
        }
        break;

        case OpCode::CALL: {
            std::string funcName = ins.argument;
            cmd.execute = [funcName](State &st) {
                auto itF = st.functionMap.find(funcName);
                if (itF == st.functionMap.end())
                    throw std::runtime_error("CALL: unknown function " + funcName);
                st.callStack.push_back(st.pc + 1);
                return itF->second + 1;
            };
        }
        break;

        case OpCode::RETURN: {
            cmd.execute = [](State &st) {
                if (st.callStack.empty()) {
                    st.running = false;
                    return -1;
                } else {
                    int retAddr = st.callStack.back();
                    st.callStack.pop_back();
                    return retAddr;
                }
            };
        }
        break;

        default:
            break;
    }
    return cmd;
}


void Interpreter::optimizeCommand(int pc) {
    const Instruction &ins = code[pc];

    switch (ins.op) {

    case OpCode::ADD:
    case OpCode::SUB:
    case OpCode::MUL:
    case OpCode::DIV:
    case OpCode::MOD:
    {
        OpCode opcode = ins.op;
        commands[pc].execute = [opcode](State &st) {
            Value vb = st.stack.back();
            st.stack.pop_back();
            Value va = st.stack.back();
            st.stack.pop_back();

            long long a = std::get<long long>(va);
            long long b = std::get<long long>(vb);

            long long result = 0;
            switch (opcode) {
            case OpCode::ADD: result = a + b; break;
            case OpCode::SUB: result = a - b; break;
            case OpCode::MUL: result = a * b; break;
            case OpCode::DIV:
                result = a / b;
                break;
            case OpCode::MOD:
                result = a % b;
                break;
            default: break;
            }

            st.stack.push_back(result);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::DECLARE_VAR:
    {
        size_t spacePos = ins.argument.find(' ');
        if (spacePos == std::string::npos) {
            commands[pc].execute = [](State &st){
                return st.pc + 1;
            };
            break;
        }

        std::string varType = ins.argument.substr(0, spacePos);
        std::string varName = ins.argument.substr(spacePos + 1);

        commands[pc].execute = [varType, varName](State &st) {
            if (varType == "int") {
                st.variables[varName] = (long long)0;
            }
            else if (varType == "bool") {
                st.variables[varName] = false;
            }
            return st.pc + 1;
        };
        break;
    }

    case OpCode::DECLARE_ARRAY:
    {
        std::string arrName = ins.argument;
        commands[pc].execute = [arrName](State &st) {
            st.variables[arrName] = (GCObject*)nullptr;
            return st.pc + 1;
        };
        break;
    }

    case OpCode::ALLOCATE_ARRAY:
    {
        std::string arrName = ins.argument;
        commands[pc].execute = [arrName](State &st) {
            Value vsz = st.stack.back();
            st.stack.pop_back();
            long long sz = std::get<long long>(vsz);
            GCObject *obj = new GCObject();
            obj->resizeArray(sz);
            st.variables[arrName] = obj;
            return st.pc + 1;
        };
        break;
    }

    case OpCode::LOAD_CONST:
    {
        Value cst = parseConstant(ins.argument);
        commands[pc].execute = [cst](State &st) {
            st.stack.push_back(cst);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::STORE_VAR:
    {
        std::string varName = ins.argument;
        commands[pc].execute = [varName](State &st) {
            Value val = st.stack.back();
            st.stack.pop_back();
            st.variables[varName] = val;
            return st.pc + 1;
        };
        break;
    }

    case OpCode::LOAD_VAR:
    {
        std::string varName = ins.argument;
        commands[pc].execute = [varName](State &st) {
            Value v = st.variables[varName];
            st.stack.push_back(v);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::STORE_ARRAY:
    {
        std::string arrName = ins.argument;
        commands[pc].execute = [arrName](State &st) {
            Value vind = st.stack.back();
            st.stack.pop_back();
            Value vval = st.stack.back();
            st.stack.pop_back();

            GCObject* arr = std::get<GCObject*>( st.variables[arrName] );
            long long idx = std::get<long long>(vind);
            arr->arrayData[(size_t)idx] = vval;
            return st.pc + 1;
        };
        break;
    }

    case OpCode::LOAD_ARRAY:
    {
        std::string arrName = ins.argument;
        commands[pc].execute = [arrName](State &st) {
            Value vind = st.stack.back();
            st.stack.pop_back();
            GCObject* arr = std::get<GCObject*>( st.variables[arrName] );
            long long idx = std::get<long long>(vind);
            Value val = arr->arrayData[(size_t)idx];
            st.stack.push_back(val);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::PRINT:
    {
        commands[pc].execute = [](State &st) {
            Value top = st.stack.back();
            st.stack.pop_back();
            if (std::holds_alternative<long long>(top)) {
                std::cout << std::get<long long>(top) << "\n";
            } else if (std::holds_alternative<bool>(top)) {
                std::cout << (std::get<bool>(top)?"true":"false")<<"\n";
            } else {
                std::cout<<"[object]\n";
            }
            return st.pc + 1;
        };
        break;
    }

    case OpCode::EQ:
    {
        commands[pc].execute = [](State &st) {
            Value vb= st.stack.back();
            st.stack.pop_back();
            Value va= st.stack.back();
            st.stack.pop_back();
            bool eq= false;
            switch(va.index()) {
            case 0: eq=(std::get<long long>(va)==std::get<long long>(vb)); break;
            case 1: eq=(std::get<bool>(va)==std::get<bool>(vb)); break;
            case 2: eq=(std::get<GCObject*>(va)==std::get<GCObject*>(vb)); break;
            }
            st.stack.push_back(eq);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::GT:
    case OpCode::LT:
    case OpCode::LEQ:
    case OpCode::GEQ:
    {
        OpCode opcode = ins.op;
        commands[pc].execute = [opcode](State &st) {
            Value vb= st.stack.back();
            st.stack.pop_back();
            Value va= st.stack.back();
            st.stack.pop_back();
            long long a= std::get<long long>(va);
            long long b= std::get<long long>(vb);
            bool r=false;
            switch(opcode) {
            case OpCode::GT:  r=(a>b);  break;
            case OpCode::LT:  r=(a<b);  break;
            case OpCode::LEQ: r=(a<=b); break;
            case OpCode::GEQ: r=(a>=b); break;
            default: break;
            }
            st.stack.push_back(r);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::AND:
    {
        commands[pc].execute= [](State &st){
            Value vb= st.stack.back(); st.stack.pop_back();
            Value va= st.stack.back(); st.stack.pop_back();
            bool r= std::get<bool>(va) && std::get<bool>(vb);
            st.stack.push_back(r);
            return st.pc + 1;
        };
        break;
    }
    case OpCode::OR:
    {
        commands[pc].execute= [](State &st){
            Value vb= st.stack.back(); st.stack.pop_back();
            Value va= st.stack.back(); st.stack.pop_back();
            bool r= std::get<bool>(va) || std::get<bool>(vb);
            st.stack.push_back(r);
            return st.pc + 1;
        };
        break;
    }

    case OpCode::JUMP:
    {
        auto itL = state.labelMap.find(ins.argument);
        if (itL== state.labelMap.end()) {
            commands[pc].execute= [](State &st){
                return st.pc+1;
            };
            break;
        }
        int target= itL->second;
        commands[pc].execute= [target](State &st){
            return target;
        };
        break;
    }

    case OpCode::JUMP_IF_FALSE:
    {
        auto itL = state.labelMap.find(ins.argument);
        if (itL== state.labelMap.end()) {
            break;
        }
        int targetPc= itL->second;
        commands[pc].execute= [targetPc](State &st){
            Value c= st.stack.back();
            st.stack.pop_back();
            bool cond= std::get<bool>(c);
            return cond? (st.pc+1) : targetPc;
        };
        break;
    }

    case OpCode::CALL:
    {
        auto itF= state.functionMap.find(ins.argument);
        if (itF==state.functionMap.end()) {
            break;
        }
        int funcPc= itF->second + 1;
        commands[pc].execute= [funcPc](State &st){
            st.callStack.push_back(st.pc+1);
            return funcPc;
        };
        break;
    }

    case OpCode::RETURN:
    {
        commands[pc].execute= [](State &st){
            if (st.callStack.empty()) {
                st.running= false;
                return -1;
            }
            int ret= st.callStack.back();
            st.callStack.pop_back();
            return ret;
        };
        break;
    }

    default:
        break;
    }
}

