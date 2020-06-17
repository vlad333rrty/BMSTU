#include <iostream>
#include <vector>
#include <set>
#include <cmath>
using namespace std;

vector<long> eratosphen(long n){
    int i,j,root=sqrt(n)+2,t;
    vector<int> a(root);
    vector<long> res;
    for (i=2;i*i<root;i++){
        if (a[i] == 0){
            for (j=i*i;j<root;j+=i){
                a[j]=1;
            }
        }
    }

    a[1]=1;

    for (i=1;i<root;i++){
        if (n%i==0){
            if (!a[i]) res.push_back(i);
            t=n/i;
            if (t%2 && t>1){
                for (j=3;j*j<=t && t%j!=0;j+=2);
                if (j*j>t) res.push_back(t);
            }

        }
    }
    return res;
}

vector<string> res;
vector<long> a;
set<string> uset;

string rec(long n){
    for (auto i:a){
        if (i>n) break;
        if (!(n%i)){
            int t=n/i;
            string s=to_string(n)+" -- "+to_string(t)+"\n";
            if (uset.insert(s).second){
                res.push_back(s);
                rec(t);
            }
        }
    }
    return "";
}

int main(){
    long n;
    cin>>n;
    a=eratosphen(n);
    res.push_back("graph {\n1\n");
    rec(n);
    res.push_back("}\n");
    for (string s:res) cout<<s;
}
