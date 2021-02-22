#include "../modules/Parser.h"

Parser::Parser(const std::string &translatedCode,const std::string &grammarFileName) {
    tokenizer=Tokenizer(translatedCode);
    grammar=Grammar(grammarFileName);
}

Parser::Status Parser::parse() {
    StateStack stack;
    grammar.getFirst();
    grammar.getFollow();
    return parseRec(stack) ? SUCCESS : FAIL;
}

bool canReduceByRule(const std::string &word,const std::string &rule){
    if (rule.length()>word.length()) return false;
    return word.compare(word.length()-rule.length(),rule.length(),rule)==0;
}

bool stackIsInFinalState(StateStack &stack){
    return stack.getCurrentWord()==Grammar::startSymbol;
}

StateStack getReducedStack(const StateStack &stack,const std::string &leftSide,const std::string &rightSide){
    StateStack reducedStack=stack;
    int len=rightSide.length();
    reducedStack.pop(len);
    reducedStack.push(leftSide);
    reducedStack.lastOp=StateStack::REDUCE;
    return reducedStack;
}

bool Parser::parseRec(StateStack stack) {
    if (parseReduced(stack)){
        return true;
    }
    if (stack.lastOp==StateStack::REDUCE){
        std::unordered_set<std::string> *follow=&grammar.getFollow()[stack.peek()];
        auto t=follow->find(Token::getTranslatedRepresentation(tokenizer.peek()));
        if (t==follow->end()){
            return false;
        }
    }
    Token::Type token=tokenizer.getNextToken(stack.getPos());
    stack.setPos(tokenizer.getPos());
    switch (token) {
        case Token::$END:
            return stackIsInFinalState(stack);
        case Token::$ERROR:
            return false;
        default:
            stack.push(Token::getTranslatedRepresentation(token));
            stack.lastOp=StateStack::SHIFT;
            return parseRec(stack);
    }
}

bool Parser::parseReduced(StateStack &stack) {
    Token::Type next=tokenizer.peek();
    for (auto &rule:grammar.getRules()) {
        std::unordered_set<std::string> *follow=&grammar.getFollow()[rule.first];
        auto t=follow->find(Token::getTranslatedRepresentation(next));
        for (const std::string &s:rule.second){
            if (canReduceByRule(stack.getCurrentWord(),s) && t!=follow->end()){
                if (parseRec(getReducedStack(stack,rule.first,s))){
                    return true;
                }
            }
        }
    }
    return false;
}