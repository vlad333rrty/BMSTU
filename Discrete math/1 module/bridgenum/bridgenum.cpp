#include <iostream>
#include <vector>
using namespace std;

struct Vertex{
    vector<int> next;
    bool flag= false;
};

int count=0,c=0;
int *t_in,*t;

void dfs(Vertex *vertices,int v,int prev){
    vertices[v].flag= true;
    t_in[v]= t[v]=c++;
    for (int i=0;i<vertices[v].next.size();i++){
        int next=vertices[v].next[i];
        if (next!=prev){
            if (vertices[next].flag) t[v]=min(t[v], t_in[next]);
            else{
                dfs(vertices,next,v);
                t[v]=min(t[v], t[next]);
                if (t[next] > t_in[v]) count++;
            }
        }
    }
}

int main(){
    int n,m;
    cin>>n>>m;
    Vertex vertices[n];
    for (int a,b;m>0;m--){
        cin>>a>>b;
        vertices[a].next.push_back(b);
        vertices[b].next.push_back(a);
    }
    t_in=new int[n];
    t=new int[n];
    for (int i=0;i<n;i++){
        if (!vertices[i].flag) dfs(vertices,i,-1);
    }
    delete[](t_in);
    delete[](t);
    cout<<count;
}
