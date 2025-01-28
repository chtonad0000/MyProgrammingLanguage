#pragma once

#include <string>

enum class OpCode {
    START_PROGRAM,
    END_PROGRAM,
    FUNC_BEGIN,
    FUNC_END,
    DECLARE_VAR,
    DECLARE_ARRAY,
    LOAD_CONST,
    STORE_VAR,
    LOAD_VAR,
    ADD, SUB, MUL, DIV, MOD,
    EQ, LT, GT, LEQ, GEQ,
    JUMP,
    JUMP_IF_FALSE,
    LABEL,
    CALL,
    RETURN,
    ALLOCATE_ARRAY,
    STORE_ARRAY,
    LOAD_ARRAY,
    PRINT,
    AND,
    OR,
    UNKNOWN
};

struct Instruction {
    OpCode op;
    std::string argument;
};
