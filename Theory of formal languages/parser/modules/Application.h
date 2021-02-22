//
// Created by vlad333rrty on 06.01.2021.
//

#ifndef LAB8_APPLICATION_H
#define LAB8_APPLICATION_H

#include <string>
#include "Lexer.h"
#include "Parser.h"

class Application {
private:
    std::string codeFileName,grammarFileName;
public:
    Application(const std::string &codeFileName, const std::string &grammarFileName);
    void start();
};


#endif //LAB8_APPLICATION_H
