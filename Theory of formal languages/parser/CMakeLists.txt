cmake_minimum_required(VERSION 3.17)
project(lab8)

set(CMAKE_CXX_STANDARD 17)

add_executable(lab8 main.cpp Application.cpp io/FileReadUtils.cpp io/FileReadUtils.h core/Lexer.cpp modules/Lexer.h data/Token.cpp data/Token.h core/Parser.cpp modules/Parser.h modules/Application.h data/Grammar.cpp data/Grammar.h core/Tokenizer.cpp modules/Tokenizer.h core/StateStack.cpp modules/StateStack.h)