#include <iostream>
#include <cmath>
#include <queue>
using namespace std;

struct Edge{
    int first,second,len;
    Edge(int s,int e,int l): first(s), second(e), len(l){};
    bool operator<(const Edge &edge) const{
        return len>edge.len;
    }
    Edge &operator=(const Edge &edge){
        if (this!=&edge){
            first=edge.first;
            len=edge.len;
            second=edge.second;
        }
        return *this;
    }

    Edge(const Edge &edge){
        first=edge.first;
        second=edge.second;
        len=edge.len;
    }
};


struct Vertex{
    vector<Edge> edges;
    int flag=0;
};


int prim(Vertex *vertices,Edge edge){
    priority_queue<Edge> prq;
    int res=edge.len;
    vertices[edge.first].flag=1;
    vertices[edge.second].flag=1;
    for (Edge e:vertices[edge.first].edges)prq.push(e);
    for (Edge e:vertices[edge.second].edges)prq.push(e);
    while (!prq.empty()){
        Edge e=prq.top();
        prq.pop();
        Vertex v=vertices[e.first],u=vertices[e.second];
        if (v.flag && !u.flag){
            vertices[e.second].flag=1;
            for (Edge a:u.edges) prq.push(a);
            res+=e.len;
        }
        if (!v.flag && u.flag){
            vertices[e.first].flag=1;
            for (Edge a:v.edges)prq.push(a);
            res+=e.len;
        }
    }
    return res;
}

int main(){
    int n,m,u,v,l;
    cin>>n>>m;
    Vertex vertices[n];
    for (int i=0;i<m;i++){
        cin>>u>>v>>l;
        Edge edge=Edge(u,v,l);
        vertices[u].edges.push_back(edge);
        vertices[v].edges.push_back(edge);
    }
    if (m==0){
        cout<<0;
    }else{
        Edge start=vertices[0].edges[0];
        for (Edge e:vertices[0].edges){
            if (e.len<start.len) start=e;
        }
        cout<<prim(vertices,start);
    }
}
