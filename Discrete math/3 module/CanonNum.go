package main

import "fmt"

type Graph struct {
        delta [][]int
	phi [][]string
	mark []bool
}

func (g *Graph) init(n int,m int){
	g.delta=make([][]int,n);
	g.phi=make([][]string,n);
	g.mark=make([]bool,n)
}
var seq []int
func main() {
	var n,m,start int
	_,_=fmt.Scan(&n,&m,&start)
	graph:=Graph{}
	graph.init(n,m)
	seq =make([]int,0)
	for i:=0;i<n;i++{
		graph.delta[i]=make([]int,m);
		graph.phi[i]=make([]string,m);
	}
	for i:=0;i<n;i++{
		for j:=0;j<m;j++{
			_,_=fmt.Scan(&graph.delta[i][j]);
		}
	}
	for i:=0;i<n;i++  {
		for j:=0;j<m;j++{
			_,_=fmt.Scan(&graph.phi[i][j]);
		}
	}
	Dfs(start,graph)
	number:=make([]int, n)
	for i:=0;i< len(seq);i++{
			number[seq[i]] = i

	}
	fmt.Print("\n", len(seq),"\n",m,"\n",number[start],"\n");
	for i:=0;i< len(seq);i++{
		for j:=0;j<m;j++ {
				fmt.Print(number[graph.delta[seq[i]][j]], " ");

		}
		fmt.Print("\n");
	}
	for i:=0;i< len(seq);i++{
		for j:=0;j<m;j++ {
				fmt.Print(graph.phi[seq[i]][j], " ");

		}
		fmt.Print("\n")
	}


}
func Dfs(v int,graph Graph){
	seq =append(seq,v)
	graph.mark[v]=true
	for i:=0;i< len(graph.delta[v]);i++{
		next:=graph.delta[v][i]
		if !graph.mark[next]{
			Dfs(next,graph)
		}
	}
}
