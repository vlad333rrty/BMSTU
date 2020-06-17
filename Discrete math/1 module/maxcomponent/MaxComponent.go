package main

import (
	"fmt"
	"github.com/skorobogatov/input"
	"strings"
)

type Component struct {
	min,edgesNum int
	vertices []int
}

func (c *Component) addVertex(vertex Vertex){
	if vertex.n<c.min{
		c.min=vertex.n
	}
	c.edgesNum+=len(vertex.next)
	c.vertices = append(c.vertices, vertex.n)
}

func (c *Component) Less(component Component)bool{
	if len(c.vertices)!= len(component.vertices){
		return len(c.vertices)< len(component.vertices)
	}
	if c.edgesNum!=component.edgesNum{
		return c.edgesNum<component.edgesNum
	}
	return c.min>component.min
}

func (c Component) hasVertex(v int) bool{
	for i:=range c.vertices{
		if c.vertices[i]==v{
			return true
		}
	}
	return false
}

func (c *Component) copy(component Component){
	c.edgesNum=component.edgesNum
	c.min=component.min
	c.vertices=c.vertices[:0]
	for i:=range component.vertices{
		c.vertices = append(c.vertices, component.vertices[i])
	}

}

type Vertex struct {
	next []*Edge
	flag bool
	n int
}

type Edge struct{
	start,end int
	flag bool
	twin *Edge
}

func dfs(vertices *[]Vertex)  *Component{
	var comp,c Component
	for i:=range *vertices{
		if !(*vertices)[i].flag{
			visitVertex(i,vertices,&c)
			if comp.Less(c) {
				comp.copy(c)
			}
			c.min=len(*vertices)
			c.edgesNum=0
			c.vertices=c.vertices[:0]
		}
	}
	return &comp
}

func visitVertex(v int,vertices *[]Vertex,c *Component){
	c.addVertex((*vertices)[v])
	(*vertices)[v].flag=true
	for i:=range (*vertices)[v].next{
		next:=(*vertices)[v].next[i]
		if !(*vertices)[next.end].flag{
			visitVertex(next.end,vertices,c)
		}
	}
}

func findComp(vertices *[]Vertex) string{
	res:=strings.Builder{}
	res.WriteString("graph {\n")
	comp:=dfs(vertices)

	for i:=range *vertices{
		res.WriteString(fmt.Sprintf("%v ",i))
		if comp.hasVertex(i){
			res.WriteString("[ color = red ]")
		}
		res.WriteString("\n")
		(*vertices)[i].flag=false
	}

	for i:=range *vertices{
		if !(*vertices)[i].flag{
			if comp.hasVertex(i){
				printEdges(&res,vertices,i,true)
			}else{
				printEdges(&res,vertices,i,false)
			}
		}
	}
	res.WriteString("}\n")
	return res.String()
}

func printEdges(res *strings.Builder,vertices *[]Vertex,v int,flag bool){
	for i:=range (*vertices)[v].next{
		next:=(*vertices)[v].next[i]
		if !next.flag{
			(*vertices)[v].next[i].flag=true
			(*vertices)[v].next[i].twin.flag=true
			res.WriteString(fmt.Sprintf("%v -- %v ",v,next.end))
			if flag{
				res.WriteString(" [color = red ]")
			}
			res.WriteString("\n")
			printEdges(res,vertices,next.end,flag)
		}
	}
}

func main(){
	var n,m int
	input.Scanf("%d %d",&n,&m)
	vertices:=make([]Vertex,n)

	for i:=0;i<m;i++{
		var a,b int
		input.Scanf("%d %d",&a,&b)
		e1:=&Edge{
			start: a,
			end:   b,
			flag:  false,
			twin:  nil,
		}
		e2:=&Edge{
			start: b,
			end:   a,
			flag:  false,
			twin:  nil,
		}
		e1.twin,e2.twin=e2,e1
		vertices[a].next = append(vertices[a].next, e1)
		vertices[b].next = append(vertices[b].next, e2)
		vertices[a].n,vertices[b].n=a,b
	}
	fmt.Println(findComp(&vertices))
}

