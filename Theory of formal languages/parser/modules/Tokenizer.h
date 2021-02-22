#ifndef LAB8_TOKENIZER_H
#define LAB8_TOKENIZER_H

#include <string>
#include "../data/Token.h"

class Tokenizer {
private:
    int pos;
    std::vector<Token::Type> tokens;
    Token::Type getNextToken();
public:
    Tokenizer()=default;
    explicit Tokenizer(const std::string &s);
    Token::Type getNextToken(int pos);
    Token::Type getNextToken(const std::string &s);
    Token::Type peek();
    int getPos();
    void setPos(int pos);
};


#endif //LAB8_TOKENIZER_H
