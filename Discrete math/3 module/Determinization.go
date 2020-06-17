package main

import ("fmt"
"sort"
        "strconv"
)
type Edge struct {
	to int
	label string
}

type Vertex struct {
	number int
	list []Edge
}


func (v *Vertex) init(i int){
	v.number=i
	v.list=make([]Edge,0)
}
type Graph struct {
	vertex []Vertex
	cap int
}
func (graph *Graph) init(n int){
	graph.vertex=make([]Vertex,n)
	for i:=0;i<n;i++{
		graph.vertex[i].init(i)
	}
	graph.cap=n
}

var states []int
var X []string
func main() {
	var n,m int
	_,_=fmt.Scan(&n,&m)
	g:=Graph{}
	g.init(n)
	X=make([]string,0)
	for i:=0;i<m;i++ {
		var a, b int
		var s string
		_, _ = fmt.Scan(&a, &b, &s)
		g.vertex[a].list = append(g.vertex[a].list, Edge{b, s})
		addX(s)
	}
	states=make([]int,n)
	for i:=0;i<n;i++{
		_,_=fmt.Scan(&states[i])
	}
	var start int
	_,_=fmt.Scan(&start)
	DET(&g,g.vertex[start])
}

func addX(str string){
	for _,s:=range X{
		if s==str{
			return
		}
	}
	if str!="lambda" {
		X = append(X, str)
	}
}

func find(vertex []Vertex,v Vertex)bool{
	for _,x:=range vertex{
		if x.number==v.number{
			return true
		}
	}
	return false
}

func closure(z []Vertex,g *Graph) []Vertex{
	vertex:=make([]Vertex,0);
	for _,q:=range z{
		DFS(q,&vertex,g)
	}

	return vertex
}

func DFS(q Vertex,vertex *[]Vertex,g *Graph) {
	if !find(*vertex, q) {
		*vertex=append(*vertex,q)
		for _, e := range g.vertex[q.number].list {
			if e.label == "lambda" {
				DFS(g.vertex[e.to], vertex, g)
			}
		}
	}
}


type Hash struct {
	tag bool
	z *[]Vertex
	i int
}



var hash []Hash

func hashFunc(v []Vertex) int{
        c:=0
	mult:=1
	s:=0
	for _,x:=range v{
		mult*=x.number
		c+=x.number
		s+=x.number*x.number
	}
	c+=mult
	c+=s
	return c%100
}

func DET(g *Graph,q Vertex){
	hash=make([]Hash,100)
	q0:=make([]Vertex,0)
	q0=append(q0,q)
	q0=closure(q0,g)
	Q:=make([][]Vertex,0)
	Q=append(Q,q0)
	stack:=make([][]Vertex,0)
	F:=make([][]Vertex,0)
	delta:=make([][]string,100)
	for i:=0;i<len(delta);i++{
		delta[i]=make([]string, 100)
	}
	stack=append(stack,q0)
	hash[hashFunc(q0)]=Hash{true,&q0,0}
	ind:=1
	for ;len(stack)>0;{
		z:=stack[0]
		for _,x:=range z{
			if states[x.number]==1{
				F=append(F,z)
				break
			}
		}
		for _,a:=range X{
			temp:=make([]Vertex,0)
			for _,x:=range z{
				for _,next:=range x.list{
					if next.label==a{
						temp=append(temp,g.vertex[next.to])
					}
				}
			}
			temp=closure(temp,g)
			t:=hashFunc(temp)
			if !hash[t].tag {
				Q = append(Q, temp)
				stack = append(stack, temp)
				hash[t] = Hash{true, &temp, ind}
				ind++
			}
			delta[hash[hashFunc(z)].i][hash[t].i]+=a+", "
		}
		stack=stack[1:]
	}
	compose(Q,F,delta)
}
func compose(Q [][]Vertex,F [][]Vertex,delta [][]string){
	final:=make([]bool,len(Q))
	for _,arr:=range F{
		final[hash[hashFunc(arr)].i]=true
	}
	fin:="digraph {\nrankdir = LR\ndummy [label = \"\", shape = none]\n"
	for _,z:=range Q{
		sort.Slice(z, func(i, j int) bool {
			return z[i].number<z[j].number
		})
	}
	for i:=0;i<len(Q);i++{
		fin+=strconv.Itoa(i)+" [ label =\"["
		for j:=0;j<len(Q[i]);j++ {
			if j == len(Q[i])-1 {
				fin += strconv.Itoa(Q[i][j].number)
			} else {
				fin += strconv.Itoa(Q[i][j].number) + " "
			}
		}
		fin+="]\", shape = "
		if final[i]{
			fin+="doublecircle]\n"
		}else{
			fin+="circle]\n"
		}
	}
	fin+="dummy -> 0\n"
	for i:=0;i<len(Q);i++{
		for j:=0;j<len(delta[i]);j++ {
			if delta[i][j] != "" {
				fin += strconv.Itoa(i) + " -> " + strconv.Itoa(j)+" [ label = \""+delta[i][j][:len(delta[i][j])-2]+"\"]\n"
			}
		}
	}
	fin+="}\n"
	fmt.Println(fin)
}

