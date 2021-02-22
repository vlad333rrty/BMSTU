#ifndef LAB8_PARSER_H
#define LAB8_PARSER_H

#include <string>
#include <vector>
#include "Tokenizer.h"
#include "../data/Grammar.h"
#include "StateStack.h"

class Parser {
private:
    Tokenizer tokenizer;
    Grammar grammar;
    bool parseReduced(StateStack &stack);
    bool parseRec(StateStack stack);
public:
    enum Status{
        SUCCESS,FAIL
    };
    Parser()=default;
    Parser(const std::string &translatedCode,const std::string &grammarFileName);
    Status parse();
};


#endif //LAB8_PARSER_H
