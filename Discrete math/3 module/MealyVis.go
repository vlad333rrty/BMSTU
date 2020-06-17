package main

import (
        "fmt"
	"strconv"
)

type Graph struct {
	delta [][]int
	phi [][]string
	mark []bool
}


func (graph *Graph) create(n int,m int){
	graph.mark=make([]bool,n);
	graph.delta=make([][]int,n)
	graph.phi=make([][]string,n)
	for i:=0;i<n;i++{
		graph.delta[i]=make([]int,m)
		graph.phi[i]=make([]string,m)
	}
}

func main() {
	n,m,start:=0,0,0
	_,_=fmt.Scan(&n,&m,&start)
	graph:=Graph{}
	graph.create(n,m)
	for i:=0;i<n;i++{
		for j:=0;j<m;j++{
			_,_=fmt.Scan(&graph.delta[i][j])
		}
	}
	for i:=0;i<n;i++ {
		for j := 0; j < m; j++ {
			_, _ = fmt.Scan(&graph.phi[i][j]);
		}
	}
	output :="digraph {\nrankdir = LR\ndummy [label = \"\",shape = none]\n";
	fmt.Println(toDOT(output,&graph,n,m,start))

}
func toDOT(fin string,g *Graph,n int,m int,start int) string{
	for i:=0;i<n;i++{
		fin+=strconv.Itoa(i) + "[ shape = circle ]\n";
	}
	fin+="dummy -> "+strconv.Itoa(start)+"\n";
	for i:=0;i<n;i++{
		for j:=0;j<m;j++{
			fin+=strconv.Itoa(i)+"->"+strconv.Itoa(g.delta[i][j]) + "[label = \"" +string(97+j)+"(" + g.phi[i][j]+")"+"\"]\n";
		}
	}
	return fin+"}"
}
