flex lab9.l
bison -d lab9.y
gcc lex.yy.c lab9.tab.c -w
./a.out
