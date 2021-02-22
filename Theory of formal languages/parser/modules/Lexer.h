//
// Created by vlad333rrty on 06.01.2021.
//

#ifndef LAB8_LEXER_H
#define LAB8_LEXER_H

#include <string>
#include <vector>
#include "../data/Token.h"

class Lexer {
private:
    std::string result;
    std::vector<Token> id_table;
    std::vector<Token> val_table;
    std::vector<Token> arg_table;
    std::vector<Token> target_table;
    std::vector<Token> dependence_table;
    std::vector<Token> command_table;
    std::vector<Token> flag_table;
    void normalize(std::string &s);
    void fillTables(std::string &s);
public:
    std::string translate(std::string s);
};



#endif //LAB8_LEXER_H
