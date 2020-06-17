#include <iostream>
#include <vector>
#include <unordered_set>
#include <set>
#include <map>
#include <algorithm>

using namespace std;


void get_tokens(const string &s,const unordered_set<char> &delimiters,vector<string> &tokens){
    string str;
    for (char c:s){
        if (delimiters.find(c)==delimiters.end()){
            str+=tolower(c);
        }else if (!str.empty()){
            tokens.push_back(str);
            str.clear();
        }
    }
    if (!str.empty()) tokens.push_back(str);
}

void get_type_freq(const vector<string> &tokens,map<string, int> &freqdi){
    for (string s:tokens){
        freqdi[s]++;
    }
}

void get_types(const vector<string> &tokens,vector<string> &wtypes){
    set<string> set;
    for (string s:tokens)set.insert(s);
    for (string s:set) wtypes.push_back(s);
}

void get_x_length_words(const vector<string> &wtypes,int x,vector<string> &words){
    for (string s:wtypes){
        if (s.length()>=x) words.push_back(s);
    }
}

void get_x_freq_words(const map<string, int> &freqdi,int x,vector<string> &words){
    for (auto it=freqdi.begin();it!=freqdi.end();it++){
        if (it->second>=x) words.push_back(it->first);
    }
    sort(words.begin(),words.end());
}

void get_words_by_length_dict(const vector<string> &wtypes,map<int, vector<string> > &lengthdi){
    for (string s:wtypes){
        lengthdi[s.length()].push_back(s);
    }
}

