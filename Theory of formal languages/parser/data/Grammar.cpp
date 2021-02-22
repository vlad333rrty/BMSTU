#include "Grammar.h"
#include "../io/FileReadUtils.h"
#include <utility>
#include <regex>
#include "Token.h"

std::string Grammar::startSymbol="S";

Grammar::Grammar(const std::string &fileName):fileName(fileName) {
    std::string grammar=readFile(fileName);
    grammar=std::regex_replace(grammar,std::regex(" "),"");
    std::regex startS("Start:(\\w+)");
    std::smatch match;
    std::regex_search(grammar,match,startS);
    if (!match.str(1).empty()) {
        startSymbol = match.str(1);
    }
    parseGrammar(grammar);
}

std::map<std::string, std::vector<std::string>> & Grammar::getRules() {
    return rules;
}

std::vector<std::string> collectRuleRightSide(const std::string &rightSide){
    std::regex reg("(.+?)[\\|]");
    std::vector<std::string> result;
    for (auto it=std::sregex_iterator(rightSide.begin(),rightSide.end(),reg),end=std::sregex_iterator();it!=end;it++){
        result.emplace_back(it->str(1));
    }
    return result;
}

void Grammar::parseGrammar(const std::string &grammar) {
    std::regex reg("(\\w+)->(.+)");
    for (auto it=std::sregex_iterator(grammar.begin(),grammar.end(),reg),end=std::sregex_iterator();it!=end;it++){
        rules.insert(std::make_pair(it->str(1),collectRuleRightSide(it->str(2))));
    }
//    for (auto & rule : rules){
//        std::cout<<rule.first<<" -> ";
//        for (auto &p : rule.second){
//            std::cout<<p<<" | ";
//        }
//        std::cout<<std::endl;
//    }
}

std::map<std::string,std::unordered_set<std::string>> &Grammar::getFirst() {
    if (!firstBuilt){
        buildFirst();
        firstBuilt= true;
    }
    return first;
}

std::map<std::string,std::unordered_set<std::string>> &Grammar::getFollow() {
    if (!followBuilt){
        buildFollow();
        followBuilt= true;
    }
    return follow;
}

std::unordered_set<std::string> getFirstTokens(const std::vector<std::string> &rightSide){
    std::regex reg("(\\{(.+?)(\\}))(.*)");
    std::unordered_set<std::string> result;
    std::smatch match;
    for (const std::string &s:rightSide){
        std::regex_search(s,match,reg);
        result.insert(match.str(1));
    }
    return result;
}

void Grammar::buildFirst() {
    for (auto &rule:rules){
        first.insert(std::make_pair(rule.first,getFirstTokens(rule.second)));
    }
}

std::string getNext(const std::string &s,int &i){ // non terminals are like [A-Z] or _[A-Za-z]_ todo
    if (s[i]!=Token::leftDelimiter){
        if (isupper(s[i])){
            return std::string(1,s[i++]);
        }else{
            if (s[i]!='_'){
                throw std::runtime_error("Unexpected symbol at: "+std::to_string(i));
            }
            std::string next;
            next+=s[i++];
            while (s[i]!='_'){
                next+=s[i++];
            }
            next+=s[i++];
            return next;
        }
    }else{
        std::string next;
        next+=s[i++];
        while (s[i]!=Token::rightDelimiter){
            next+=s[i++];
        }
        next+=s[i++];
        return "";
    }
}

void Grammar::buildFollow() {
    for (auto &rule:rules){
        follow.insert(std::make_pair(rule.first,std::unordered_set<std::string>()));
    }
    follow[startSymbol].insert(Token::getTranslatedRepresentation(Token::$END));
    bool setChanged;
    do{
        setChanged= false;
        for (auto &rule:rules){
            for (const std::string &rightSide:rule.second){
                int i=0;
                while (i<rightSide.length()){
                    std::string next=getNext(rightSide,i);
                    if (next.empty()){
                        continue;
                    }
                    std::unordered_set<std::string> *followNext=&follow[next];
                    int len=followNext->size();
                    if (i<rightSide.length()){
                        int j=i;
                        std::string nextLexem=getNext(rightSide,j);
                        followNext->insert(first[nextLexem].begin(),first[nextLexem].end());
                    }else{
                        followNext->insert(follow[rule.first].begin(),follow[rule.first].end());
                    }
                    if (len!=followNext->size()){
                        setChanged= true;
                    }
                }
            }
        }
    } while (setChanged);
//    for (auto & rule : follow) {
//        std::cout << rule.first << " -> ";
//        for (auto &p : rule.second) {
//            std::cout << p << " | ";
//        }
//        std::cout << std::endl;
//    }
}
