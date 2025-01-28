cmake_minimum_required(VERSION 3.29.6)
project(compiler)

set(CMAKE_CXX_STANDARD 20)

add_executable(compiler main.cpp
        Value.h
        GCObject.cpp
        GCObject.h
        MarkSweep.cpp
        MarkSweep.h
        Instruction.h
        Interpreter.cpp
        Interpreter.h
)

