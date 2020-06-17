package main

import (
        "fmt"
	"strconv"
)

type Vertex struct {
	number int
	name string
	order int
}
func (v *Vertex) init(i int,name string){
	v.number=i
	v.name=name
}
type Graph struct {
	vertex []Vertex
	delta [][]int
	phi [][]string
	cap int
}
func (graph *Graph) addVertex(i int){
	graph.vertex[i]=Vertex{}
}
func (graph *Graph) init(n int,m int){
	graph.vertex=make([]Vertex,0)
	graph.delta=make([][]int,n)
	graph.phi=make([][]string,n)
	for i:=0;i<n;i++{
		graph.delta[i]=make([]int,m)
		graph.phi[i]=make([]string,m)
	}
	graph.cap=n
}

func (g *Graph) contains(number int,name string) bool{
	for _,x:=range g.vertex{
		if x.number==number && x.name==name{
			return true
		}
	}
	return false
}

var edgeLabel []string
var something_else []string
func main() {
	var n int
	_,_=fmt.Scan(&n)
	edgeLabel=make([]string,n)
	for i:=0;i<n;i++{
		_,_=fmt.Scan(&edgeLabel[i])
	}
	var t int
	_,_=fmt.Scan(&t)
	something_else=make([]string,t)
	for i:=0;i<t;i++{
		_,_=fmt.Scan(&something_else[i])
	}
	var m int
	_,_=fmt.Scan(&m)
	m,n=n,m
	graph:=Graph{}
	graph.init(n,m)
	for i:=0;i<n;i++{
		for j:=0;j<m;j++{
			_,_=fmt.Scan(&graph.delta[i][j])
		}
	}
	for i:=0;i<n;i++{
		for j:=0;j<m;j++{
			_,_=fmt.Scan(&graph.phi[i][j])
		}
	}
	k:=0
	for i:=0;i<n;i++{
		for j:=0;j<m;j++{
			if !graph.contains(graph.delta[i][j],graph.phi[i][j]){
				graph.vertex=append(graph.vertex,Vertex{graph.delta[i][j],graph.phi[i][j],k})
				k++
			}
		}
	}
	graph.cap=len(graph.vertex)
	fin:="digraph{\nrankdir = LR\n"
	fin=compose(&graph,fin)
	fmt.Println(fin)

}

func compose(g *Graph,fin string) string{
	for i:=0;i<g.cap;i++ {
		fin+=strconv.Itoa(g.vertex[i].order)+" [ label = "+"\"("+strconv.Itoa(g.vertex[i].number)+","+g.vertex[i].name+")\"]\n"
		for j := 0; j < len(g.delta[0]); j++ {
			fin += strconv.Itoa(g.vertex[i].order) + " -> " + strconv.Itoa(g.findOrder(g.delta[g.vertex[i].number][j],g.phi[g.vertex[i].number][j]))
			fin+="[ label = \""+edgeLabel[j]+"\"]\n"
		}
	}
	fin+="}\n"
	return fin
}

func (g *Graph)findOrder(number int,name string) int{
	for _,x:=range g.vertex{
		if x.number==number && x.name==name{
			return x.order
		}
	}
	return number
}
