package main

import (
	"fmt"
	"go/ast"
	"go/format"
	"go/parser"
	"go/token"
	"log"
	"os"
	"sort"
	"strings"
)

func printTree(fset *token.FileSet,file *ast.File) {
	ast.Fprint(os.Stdout, fset, file, nil)
}

// var5 var sort
func sortVarBlocks(file *ast.File){
	ast.Inspect(file, func(node ast.Node) bool {
		if decl,ok:=node.(*ast.GenDecl);ok{
			if decl.Tok==token.VAR {
				for _,spec:=range decl.Specs {
					sort.Slice(spec.(*ast.ValueSpec).Names, func(i, j int) bool {
						s1:=fmt.Sprintf("%s",spec.(*ast.ValueSpec).Names[i])
						s2:=fmt.Sprintf("%s",spec.(*ast.ValueSpec).Names[j])
						return strings.Compare(s2,s1)>0
					})
				}
				sort.Slice(decl.Specs, func(i, j int) bool {
					s1:=fmt.Sprintf("%s", decl.Specs[i].(*ast.ValueSpec).Names[0])
					s2:=fmt.Sprintf("%s", decl.Specs[j].(*ast.ValueSpec).Names[0])
					return strings.Compare(s2,s1)>0
				})
			}
		}
		return true
	})
}

//var 21
func changeDeclaration(file *ast.File){
	decls:=make([]ast.Decl,0)

	ast.Inspect(file, func(node ast.Node) bool {
		if assignment,ok:=node.(*ast.AssignStmt);ok{
			if len(assignment.Lhs)==1{
				specs:=make([]ast.Spec,0)
				names:=make([]*ast.Ident,0)
				names = append(names, &ast.Ident{
					NamePos: assignment.TokPos,
					Name:    assignment.Lhs[0].(*ast.Ident).Name,
					Obj:     nil,
				})
				specs = append(specs, &ast.ValueSpec{
					Doc:     nil,
					Names:   names,
					Type:    nil,
					Values:  nil,
					Comment: nil,
				})
				decls = append(decls, &ast.GenDecl{
					Doc:    nil,
					TokPos: assignment.TokPos,
					Tok:    assignment.Tok,
					Lparen: 0,
					Specs:  specs,
					Rparen: 0,
				})
				node=nil
			}
		}
		return true
	})

	ast.Inspect(file, func(node ast.Node) bool {
		if block,ok:=node.(*ast.BlockStmt);ok{
			for i:=0;i<len(block.List);{
				if _,ok:=block.List[i].(*ast.AssignStmt);ok{
					remove(&block.List,i)
				}else{
					i++
				}
			}
			for i:=range decls{
				block.List = append(block.List, &ast.DeclStmt{Decl: decls[i]})
			}
			return false
		}
		return true
	})
}

//var 32
func splitVarBlocks(file *ast.File){
	decls:=make([]ast.Stmt,0)

	ast.Inspect(file, func(node ast.Node) bool {
		if block,ok:=node.(*ast.BlockStmt);ok{
			for i:=0;i<len(block.List);{
				if decl,ok:=block.List[i].(*ast.DeclStmt);ok{
					decls = append(decls, decl)
					remove(&block.List,i)
				}else{
					i++
				}
			}
		}
		return true
	})

	for i:=0;i<len(decls);{
		genDecl:=decls[i].(*ast.DeclStmt).Decl.(*ast.GenDecl)
		specs:=genDecl.Specs
		if len(specs)>1{
			temp:=make([]ast.DeclStmt,0)
			for j:=range specs{
				vs:=specs[j].(*ast.ValueSpec)
				for k:=range vs.Names{
					temp = append(temp,ast.DeclStmt{
						Decl: &ast.GenDecl{
							Doc:    nil,
							TokPos: genDecl.TokPos,
							Tok:    genDecl.Tok,
							Lparen: 0,
							Specs:  []ast.Spec{&ast.ValueSpec{
								Doc:     nil,
								Names:   []*ast.Ident{vs.Names[k]},
								Type:    nil,
								Values:  nil,
								Comment: nil,
							}},
							Rparen: 0,
						},
					})
				}
			}
			remove(&decls,i)
			for j:=range temp{
				decls = append(decls, &temp[j])
			}
		}else{
			i++
		}
	}

	ast.Inspect(file, func(node ast.Node) bool {
		if block,ok:=node.(*ast.BlockStmt);ok{
			for i:=range decls{
				block.List = append(block.List, decls[i])
			}
			return false
		}
		return true
	})
}

func remove(List *[]ast.Stmt,i int){
	copy((*List)[i:],(*List)[i+1:])
	(*List)[len(*List)-1]=nil
	*List=(*List)[:len(*List)-1]
}

func main() {
	fileName:="test.go"

	fset:=token.NewFileSet()
	if file,err:=parser.ParseFile(fset,fileName,nil,parser.ParseComments);err==nil{
		sortVarBlocks(file)
		//changeDeclaration(file)
		//splitVarBlocks(file)
		if format.Node(os.Stdout, fset, file) != nil {
			fmt.Printf("Formatter error: %v\n", err)
		}

		printTree(fset,file)
	}else{
		log.Fatal(err)
	}
}