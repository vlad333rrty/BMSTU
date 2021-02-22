
#ifndef LAB8_STATESTACK_H
#define LAB8_STATESTACK_H

#include <vector>
#include "../modules/Tokenizer.h"

class StateStack {
private:
    std::string currentString;
    std::vector<std::string> lexems;
    int pos;
public:
    enum{
        SHIFT,REDUCE
    } lastOp;
    StateStack();
    void push(const std::string& s);
    void pop(int len);
    std::string peek();
    int getPos();
    void setPos(int pos);
    uint size();
    std::string &getCurrentWord();
};


#endif //LAB8_STATESTACK_H
