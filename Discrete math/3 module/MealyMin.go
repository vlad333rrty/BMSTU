package main

import (
	"fmt"
	"github.com/skorobogatov/input"
	"strings"
)

type Mealy struct {
	delta [][]int
	phi [][]string
	vertices []Vertex
	start int
}

type Vertex struct {
	flag bool
	parent *Vertex
	depth int
	i int
}

func (mealy *Mealy) init(n,m,q0 int){
	mealy.delta,mealy.phi=make([][]int,n),make([][]string,n)
	mealy.vertices,mealy.start=make([]Vertex,n),q0
	for i:=0;i<n;i++{
		mealy.delta[i]=make([]int,m)
		mealy.phi[i]=make([]string,m)
		mealy.vertices[i].parent=&mealy.vertices[i]
		mealy.vertices[i].i=i
	}
	for i:=range mealy.delta{
		for j:=range mealy.delta[i]{
			input.Scanf("%d",&mealy.delta[i][j])
		}
	}
	for i:=range mealy.phi{
		for j:=range mealy.phi[i]{
			input.Scanf("%s",&mealy.phi[i][j])
		}
	}
}

func find(v *Vertex) *Vertex{
	if v.parent==v{
		return v
	}
	v.parent=find(v.parent)
	return v.parent
}

func union(x,y *Vertex){
	rootX,rootY:=find(x),find(y)
	if rootX.depth<rootY.depth{
		rootX.parent=rootY
	}else{
		rootY.parent=rootX
		if rootX!=rootY && rootX.depth==rootY.depth{
			rootX.depth++
		}
	}
}

func split1(mealy *Mealy)(int,[]*Vertex){
	eq,m:=false, len(mealy.vertices)
	pi:=make([]*Vertex,m)
	for i:=0;i< len(mealy.vertices);i++{
		for j:=i+1;j< len(mealy.vertices);j++{
			if find(&mealy.vertices[i])!=find(&mealy.vertices[j]){
				eq=true
				for k:=0;k< len(mealy.phi[i]);k++{
					if mealy.phi[i][k]!=mealy.phi[j][k]{
						eq=false
						break
					}
				}
				if eq{
					union(&mealy.vertices[i],&mealy.vertices[j])
					m--
				}
			}
		}
	}
	for i:=range mealy.vertices{
		pi[i]=find(&mealy.vertices[i])
	}
	return m,pi
}

func split(mealy *Mealy,pi []*Vertex) (int,[]*Vertex){
	eq,m:=false, len(mealy.vertices)
	for i:=0;i<m;i++{
		mealy.vertices[i].parent=&mealy.vertices[i]
		mealy.vertices[i].depth=0
	}
	for i:=0;i< len(mealy.vertices);i++{
		for j:=i+1;j< len(mealy.vertices);j++{
			if pi[i]==pi[j] && find(&mealy.vertices[i])!=find(&mealy.vertices[j]){
				eq=true
				for k:=0;k< len(mealy.delta[i]);k++{
					w1,w2:=mealy.delta[i][k],mealy.delta[j][k]
					if pi[w1]!=pi[w2]{
						eq=false
						break
					}
				}
				if eq{
					union(&mealy.vertices[i],&mealy.vertices[j])
					m--
				}
			}
		}
	}
	for i:=range mealy.vertices{
		pi[i]=find(&mealy.vertices[i])
	}
	return m,pi
}

func added(vertices *[]Vertex,v *Vertex)bool{
	for i:=range *vertices{
		if (*vertices)[i]==*v{
			return false
		}
	}
	*vertices = append(*vertices, *v)
	return true
}

func AufenkampHohn(mealy *Mealy) *Mealy{
	m,pi:=split1(mealy)
	m1:=0
	for ;;{
		m1,pi=split(mealy,pi)
		if m==m1{
			break
		}
		m=m1
	}
	res:=Mealy{}
	res.delta=make([][]int, len(mealy.delta))
	res.phi=make([][]string, len(mealy.phi))
	for i:=range mealy.phi{
		res.delta[i],res.phi[i]=make([]int,len(mealy.delta[i])),make([]string,len(mealy.phi[i]))
	}

	for i:=range mealy.vertices{
		q:=pi[i]
		if added(&res.vertices,q){
			for j:=0;j< len(mealy.phi[0]);j++{
				res.delta[q.i][j]=pi[mealy.delta[i][j]].i
				res.phi[q.i][j]=mealy.phi[i][j]
			}
		}
	}

	for i:=range res.vertices{
		mealy.vertices[res.vertices[i].i].i=i
		res.vertices[i].i=i
	}
	res.start=pi[mealy.start].i
	prepareMealy(&res,mealy)
	return &res
}

func prepareMealy(res *Mealy,mealy *Mealy){
	delta,phi:=make([][]int,0),make([][]string,0)
	for i:=range res.delta{
		if res.phi[i][0]!=""{
			delta = append(delta, make([]int,len(res.delta[i])))
			phi = append(phi, make([]string,len(res.phi[i])))
			copy(phi[len(phi)-1],res.phi[i])
			for j:=range delta[len(delta)-1]{
				delta[len(delta)-1][j]=mealy.vertices[res.delta[i][j]].i
			}
		}
	}
	res.delta,res.phi=delta,phi
}
var order []int
func DFS(mealy *Mealy,v int){
	mealy.vertices[v].flag=true
	order = append(order, v)
	for i:=range mealy.delta[v]{
		if !mealy.vertices[mealy.delta[v][i]].flag{
			DFS(mealy,mealy.delta[v][i])
		}
	}
}

func printMealy(mealy Mealy){
	s:=strings.Builder{}
	s.WriteString("digraph {\nrankdir=LR\ndummy [label=\"\",shape=none]\n")
	for i:=range order{
		s.WriteString(fmt.Sprintf("%v [shape = circle]\n",i))
	}
	f:=make([]int, len(mealy.delta))
	for i:=range order{
		f[order[i]]=i
	}
	s.WriteString(fmt.Sprintf("dummy -> %v\n",f[mealy.start]))
	for i:=0;i< len(order);i++{
		for j:=0;j< len(mealy.phi[i]);j++{
			s.WriteString(fmt.Sprintf("%v -> %v [label = \"%c(%v)\"]\n",i,f[mealy.delta[order[i]][j]],j+97,mealy.phi[order[i]][j]))
		}
	}

	s.WriteString("}\n")
	fmt.Println(s.String())
}

func main(){
	var n,m,q0 int
	input.Scanf("%d %d %d",&n,&m,&q0)
	mealy:=Mealy{}
	mealy.init(n,m,q0)
	r:=AufenkampHohn(&mealy)
	DFS(r,r.start)
	printMealy(*r)
}

