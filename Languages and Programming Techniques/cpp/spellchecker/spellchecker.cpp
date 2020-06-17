#include <iostream>
#include <vector>
#include <unordered_set>
#include <map>
#include <algorithm>
#include <fstream>
using namespace std;

unordered_set<string> getUnion(vector<string> &firstWord,vector<string> &secondWord){
    unordered_set<string> set;
    for (string s:firstWord) set.insert(s);
    for (string s:secondWord) set.insert(s);
    return set;
}

unordered_set<string> getIntersection(vector<string> &firstWord, vector<string> &secondWord){
    unordered_set<string> set;
    for (string s:firstWord){
        if (find(secondWord.begin(), secondWord.end(), s) != secondWord.end()) set.insert(s);
    }
    return set;
}

double getRatio(vector<string> &firstWord, vector<string> &secondWord){
    int iLen=getIntersection(firstWord, secondWord).size();
    if (iLen==0) return 0;
    int uLen=getUnion(firstWord, secondWord).size();
    return iLen/(double)uLen;
}

struct WordData{
    vector<string> bigramms;
    string word;
    int freq;
};

vector<string> makeBigramm(string &s){
    vector<string> res;
    for (int i=0;i<s.length()-1;i++){
        res.push_back(s.substr(i,2));
    }
    return res;
}

vector<WordData> makeBigramms(){
    vector<WordData> res;
    ifstream file;
    file.open("count_big.txt");
    int freq;
    string word;
    while (file >> word >> freq){
        res.push_back(WordData{makeBigramm(word),word,freq});
    }
    file.close();
    return res;
}

string getCorrection(vector<WordData> &data,vector<string> &bigramms){
    double similarity=0,ratio=0;
    int freq=0;
    string correction;
    for (int i=0;i<data.size() && ratio<1;i++){
        ratio=getRatio(bigramms,data[i].bigramms);
        if (ratio>similarity){
            similarity=ratio;
            freq=data[i].freq;
            correction=data[i].word;
        }else if (ratio==similarity){
            if (data[i].freq>freq){
                freq=data[i].freq;
                correction=data[i].word;
            }else if (data[i].freq==freq && correction.length()>data[i].word.length()){
                correction=data[i].word;
            }
        }
    }
    return correction;
}

void correctWords(){
    string s;
    vector<WordData> data=makeBigramms();
    vector<string> bigramms;
    while(cin>>s){
        bigramms=makeBigramm(s);
        cout<<getCorrection(data,bigramms)<<endl;
    }
}

int main(){
    correctWords();
    return 0;
}
