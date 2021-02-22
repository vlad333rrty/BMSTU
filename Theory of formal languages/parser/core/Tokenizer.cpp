#include "../modules/Tokenizer.h"
#include <iostream>

Tokenizer::Tokenizer(const std::string &s):pos(0) {
    Token::Type token;
    do {
        token=getNextToken(s);
        tokens.emplace_back(token);
    } while (token!=Token::$END && token!=Token::$ERROR);
    pos=0;
}

int getToken(const std::string &token);

Token::Type Tokenizer::getNextToken() {
    if (pos>=tokens.size()){
        return Token::$END; //Just in case
    }
    return tokens[pos++];
}

int Tokenizer::getPos() {
    return pos;
}

void Tokenizer::setPos(int pos) {
    this->pos=pos;
}

Token::Type Tokenizer::getNextToken(int pos) {
    setPos(pos);
    return getNextToken();
}

Token::Type Tokenizer::peek() {
    int oldPos=pos;
    Token::Type token=getNextToken();
    pos=oldPos;
    return token;
}

Token::Type Tokenizer::getNextToken(const std::string &s) {
    if (pos==s.length()) return Token::$END;
    if (s[pos]!=Token::leftDelimiter) return Token::$ERROR;
    pos++;
    std::string nextToken;
    nextToken+=s[pos++];
    while (pos<s.length() && s[pos]!=Token::rightDelimiter){
        nextToken+=s[pos++];
    }
    if (s[pos]!=Token::rightDelimiter) return Token::$ERROR;
    pos++;
    return (Token::Type)getToken(nextToken);
}

int getToken(const std::string &token){
    for (int i=0;i<Token::translatedRepresentation.size();i++){
        if (Token::translatedRepresentation[i]==token){
            return i;
        }
    }
    std::cout<<Token::translatedRepresentation.size();
    return Token::$ERROR;
}