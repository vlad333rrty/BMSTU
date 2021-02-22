#include <vector>
#include <unordered_set>
#include <map>

class AlgebraicStructure{
public:
    AlgebraicStructure(std::vector<char> alphabet, std::vector<std::vector<char>> dependencies, int n): table(n, std::vector<int>(n)){
        for (int i=0;i<alphabet.size();i++){
            intToChar[i]=alphabet[i];
            charToInt[alphabet[i]]=i;
        }
        for (int i=0; i < dependencies.size(); i++){
            for (int j=0; j < dependencies.size(); j++){
                this->table[i][j]=charToInt[dependencies[i][j]];
            }
        }
    }

    std::vector<char> getGenerators() {
        int p = 1 << table.size();
        std::vector<int> result;
        for (int i = 0; i < p; i++) {
            std::vector<int> gens;
            for (int j = 0; j < table.size(); j++) {
                if (((i >> j) & 1) == 1) {
                    gens.push_back(j);
                }
            }
            if (doesGenerate(gens) && (result.size() > gens.size() || result.empty())) {
                result = gens;
            }
        }
        std::vector<char> generators;
        generators.reserve(result.size());
        for (int i:result) {
            generators.push_back(intToChar[i]);
        }
        return generators;
    }

    bool isAssociative(){
        for (auto & i : table){
            for (int j=0;j<table.size();j++){
                for (int k=0;k<table.size();k++){
                    if (i[table[j][k]]!=table[i[j]][k]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    bool checkLeftUnity(){
        for (int i=0,j;i<table.size();i++){
            for (j=0;j<table.size();j++){
                if (table[i][j]!=j){
                    break;
                }
            }
            if (j==table.size()) return true;
        }
        return false;
    }

    bool checkRightUnity(){
        for (int i=0,j;i<table.size();i++){
            for (j=0;j<table.size();j++){
                if (table[j][i]!=j){
                    break;
                }
            }
            if (j==table.size()) return true;
        }
        return false;
    }
private:
    std::vector<std::vector<int>> table;
    std::map<int,char> intToChar;
    std::map<char,int> charToInt;
    bool doesGenerate(std::vector<int> gens){
        std::unordered_set<int> tempSet;
        for (int i=0;i<gens.size();i++){
            for (int j=0;j<gens.size();j++){
                tempSet.insert(table[gens[i]][gens[j]]);
            }
        }
        return tempSet.size() == table.size();
    }
};