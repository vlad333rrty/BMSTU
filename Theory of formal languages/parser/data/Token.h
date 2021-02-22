#ifndef LAB8_TOKEN_H
#define LAB8_TOKEN_H

#include <string>
#include <vector>

class Token{
private:
    std::string representation;
public:
    static char leftDelimiter;
    static char rightDelimiter;
    static std::vector<std::string> translatedRepresentation;
    enum Type{
        ID,VAL,ARG,TARGET,FLAG,DEPENDENCY,COMMAND,COLON,EQUALS,DOLLAR,RIGHT_P,LEFT_P,LEFT_SQ_P,RIGHT_SQ_P,IF,THEN,ELSE,FI,
        FOR,IN,DO,DONE,GREATER_OR_EQUAL,LESS_OR_EQUAL,LESS,GREATER,EQUAL,TAB,NEWLINE,$END,$ERROR
    };
    explicit Token(std::string &representation);
    std::string getRepresentation();
    std::string static getTranslatedRepresentation(Type type);
};

#endif //LAB8_TOKEN_H
