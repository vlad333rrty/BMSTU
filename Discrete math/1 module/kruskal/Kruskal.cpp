#include <iostream>
#include <cmath>
#include <queue>
using namespace std;

struct Vertex{
    int x,y,depth;
    Vertex *parent;
    Vertex(int x,int y){
        this->x=x;
        this->y=y;
        parent=this;
        depth=0;
    }
};

struct Edge{
    double len;
    Vertex *start,*end;
    Edge(Vertex *s,Vertex *e):start(s),end(e){
        len=sqrt((s->x-e->x)*(s->x-e->x)+(s->y-e->y)*(s->y-e->y));
    }
    bool operator<(const Edge &edge) const{
        return this->len>edge.len;
    }
    Edge(const Edge &edge){
        start=edge.start;
        end=edge.end;
        len=edge.len;
    }
    Edge &operator=(const Edge &edge){
        if (this!=&edge){
            len=edge.len;
            start=edge.start;
            end=edge.end;
        }
        return *this;
    }
};

Vertex *find(Vertex *v){
    if (v->parent==v) return v;
    v->parent=find(v->parent);
    return v->parent;
}

void Union(Vertex *x,Vertex *y){
    Vertex *p_x=find(x),*p_y=find(y);
    if (p_x->depth<p_y->depth){
        p_x->parent=p_y;
    }else{
        p_y->parent=p_x;
        if (p_x!=p_y && p_x->depth==p_y->depth) p_x->depth++;
    }
}

double kruskal(Vertex **vertices,priority_queue<Edge> &q,int n){
    double count=0;
    for (int i=0;i<n-1 && !q.empty();){
        Edge edge=q.top();
        q.pop();
        if (find(edge.start)!=find(edge.end)){
            count+=edge.len;
            Union(edge.start,edge.end);
            i++;
        }
    }
    return count;
}

int main(){
    priority_queue<Edge> q;
    int n,x,y;
    cin>>n;
    Vertex *vertices[n];
    for (int i=0;i<n;i++){
        cin>>x>>y;
        vertices[i]=new Vertex(x,y);
    }
    for (y=0;y<n;y++){
        for (x=y+1;x<n;x++){
            q.push(Edge(vertices[y], vertices[x]));
        }
    }
    printf("%.2f",kruskal(vertices,q,n));
    for (x=0;x<n;x++){
        delete(vertices[x]);
    }
}
