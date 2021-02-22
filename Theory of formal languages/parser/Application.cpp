#include "modules/Application.h"
#include "io/FileReadUtils.h"
#include <iostream>

Application::Application(const std::string &codeFileName, const std::string &grammarFileName) : codeFileName(codeFileName),grammarFileName(grammarFileName) {}

void Application::start() {
    std::string s=readFile(codeFileName);
    Lexer lexer;
    std::string translatedCode=lexer.translate(s);
    std::cout<<translatedCode<<std::endl;
    Parser parser=Parser(translatedCode,grammarFileName);
    if (parser.parse()==Parser::SUCCESS){
        std::cout<<"Wonderful"<<std::endl;
    }else{
        std::cout<<":("<<std::endl;
    }
}