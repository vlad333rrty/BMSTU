#include <iostream>
#include <queue>
using namespace std;

struct Vertex{
    vector<int> next;
    bool flag= false;
};

void BFS(vector<int> &dist,Vertex *vertices,int v){
    dist[v]=0;
    queue<int> q;
    q.push(v);
    vertices[v].flag=true;
    while (!q.empty()){
        int u=q.front();
        q.pop();
        for (int i=0;i<vertices[u].next.size();i++){
            int w=vertices[u].next[i];
            if (!vertices[w].flag){
                vertices[w].flag=true;
                dist[w]=dist[u]+1;
                q.push(w);
            }
        }
    }
}

int main(){
    int n,m,k;
    cin>>n>>m;
    Vertex vertices[n];
    for (int i=0,x,y;i<m;i++){
        cin>>x>>y;
        vertices[x].next.push_back(y);
        vertices[y].next.push_back(x);
    }
    cin>>k>>m;
    vector<int> a(n),b(n);
    BFS(a,vertices,m);
    for (int i=0;i<n;i++) vertices[i].flag= false;
    for (int i=1,j;i<k;i++){
        cin>>m;
        BFS(b,vertices,m);
        for (j=0;j<n;j++){
            if (a[j]!=b[j] || a[j]==0) a[j]=-1;
        }
        for (j=0;j<n;j++) vertices[j].flag=false;
        for (j=0;j<n;j++) b[j]=0;
    }
    b.clear();
    for (int i=0;i<n;i++) if (a[i]!=-1) b.push_back(i);
    if (b.size()==0) cout<<'-';
    else for (int i:b) cout<<i<<" ";
}
