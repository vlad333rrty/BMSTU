#include <stdexcept>
#include "Token.h"

char Token::leftDelimiter='{';
char Token::rightDelimiter='}';

std::vector<std::string> Token::translatedRepresentation={
        "id","val","arg","target","flag","dependency","command",":","=","$",")","(","[","]",
        "if","then","else","fi","for","in","do","done",">=","<=","<",">","==","tab","nl","$end","$error"
};

Token::Token(std::string &representation):representation(representation) {}

std::string Token::getRepresentation() {
    return representation;
}

std::string Token::getTranslatedRepresentation(Type type) {
    return Token::leftDelimiter+translatedRepresentation[type]+Token::rightDelimiter;
}