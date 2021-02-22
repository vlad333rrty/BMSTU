#ifndef LAB8_GRAMMAR_H
#define LAB8_GRAMMAR_H

#include <map>
#include <vector>
#include <unordered_set>

class Grammar {
private:
    std::map<std::string,std::vector<std::string>> rules;
    std::map<std::string,std::unordered_set<std::string>> first;
    std::map<std::string,std::unordered_set<std::string>> follow;
    std::string fileName;
    bool firstBuilt=false;
    bool followBuilt= false;
    void parseGrammar(const std::string &grammar);
    void buildFirst();
    void buildFollow();
public:
    static std::string startSymbol;
    Grammar()=default;
    explicit Grammar(const std::string &fileName);
    std::map<std::string,std::vector<std::string>>& getRules();
    std::map<std::string,std::unordered_set<std::string>>& getFirst();
    std::map<std::string,std::unordered_set<std::string>>& getFollow();
};


#endif //LAB8_GRAMMAR_H
