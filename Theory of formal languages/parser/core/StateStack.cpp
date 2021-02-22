#include "../modules/StateStack.h"

StateStack::StateStack():pos(0) {}

void StateStack::push(const std::string& s) {
    currentString+=s;
    lexems.push_back(s);
}

uint StateStack::size() {
    return currentString.size();
}

int StateStack::getPos() {
    return pos;
}

void StateStack::setPos(int pos) {
    this->pos=pos;
}

std::string &StateStack::getCurrentWord() {
    return currentString;
}

void StateStack::pop(int len) {
    currentString=currentString.substr(0, currentString.length() - len);
    while (len>0){
        int poppedLen=lexems.back().length();
        lexems.pop_back();
        len-=poppedLen;
    }
}

std::string StateStack::peek() {
    return lexems.back();
}
