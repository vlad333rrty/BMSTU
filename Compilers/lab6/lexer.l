%option noyywrap bison-bridge bison-locations
%{
#include <stdio.h>
#include <stdlib.h>

#define TAG_NUMBER 1
#define TAG_REGULAR_STRING 2
#define TAG_LITERAL_STRING 3
#define TAG_CHAR 4

char *tagNames[]={
    "END","NUMBER","REGULAR STRING","LITERAL STRING"
};

struct Position{
    int line, pos, index;
};

typedef struct Position Position;

void printPos(Position *p){
    printf("(%d, %d)",p->line, p->pos);
}

struct Fragment{
    Position start,follow;
};

typedef struct Fragment YYLTYPE;

void printFragment(YYLTYPE *type){
    printPos(&(type->start));
    printf(" - ");
    printPos(&(type->follow));
}

union Token{
    char *ident;
    long num;
    char ch;
};

typedef union Token YYSTYPE;

int continued;
Position curPos;

#define YY_USER_ACTION {   \
        int i;\
        if (!continued) yylloc -> start = curPos;\
        continued=0;\
        for (i=0;i<yyleng;i++){\
            if (yytext[i]=='\n'){\
                curPos.line++;\
                curPos.pos=1;\
            }else{\
                curPos.pos++;\
            }\
            curPos.index++;\
        }\
        yylloc->follow=curPos;\
    }

void init(char *program){
    continued=0;
    curPos.line=1;
    curPos.pos=1;
    curPos.index=0;
    yy_scan_string(program);
}

void printError(char *message){
    printf("Error ");
    printPos(&curPos);
    printf(": %s\n",message);
}

void unexpectedCharError(char *yytext){
    printf("Error ");
    printPos(&curPos);
    printf(" Unexpected character: %s\n",yytext);
}

void unexpectedCharErrorExpected(char *yytext,char *expected){
    printf("Error ");
    printPos(&curPos);
    printf(" Unexpected character: %s. %s expected",yytext,expected);

}

void replaceDoubleQuoteInLiteralString(char *str,int len,char *buf){
    int j=0;
    for (int i=2;i<len-1;){ // from 2 to len-2 because the occurrence of double quote in the very start/end isn't possible
        if (str[i]=='\"' && str[i]==str[i+1]){
            buf[j++]='\"';
            i+=2;
        }else{
            buf[j++]=str[i++];
        }
    }
    buf[j]='\0';
}

void interpretRegularString(char *str,int len,char *buf){
    int j=0;
    for (int i=1;i<len-1;){
        if (str[i]=='\\'){
            if (str[i+1]=='n'){
                buf[j++]='\n';
            }else if (str[i+1]=='t'){
                buf[j++]='\t';
            }else if (str[i+1]=='\"'){
                buf[j++]='\"';
            }
            i+=2;
        }else{
            buf[j++]=str[i++];
        }
    }
    buf[j]='\0';
}

%}

NUMBER (0|1+)
REGULAR_STRING (\"(\\\"|\t|\n|\\n|\\t|[^\\\"\t\n])*\")
LITERAL_STRING (@\"(\"\"|[^\"])*\")

%x REG_STRING LIT_STRING_START LIT_STRING

%%

[\n\t ]+

\"                      {
                            BEGIN(REG_STRING);
                            return TAG_CHAR;
                        }
<REG_STRING>\"          {
                            BEGIN(0);
                            return TAG_CHAR;
                        }
<REG_STRING>.           {
                            return TAG_CHAR;
                        }
<REG_STRING><<EOF>>     {
                            printError("Unexpected EOF. \" expected");
                            return 0;
                        }
@                       {
                            BEGIN(LIT_STRING_START);
                            return TAG_CHAR;
                        }
<LIT_STRING_START>[^\"] {
                            unexpectedCharErrorExpected(yytext,"\"");
                            return TAG_CHAR;
                        }
<LIT_STRING_START>\"    {
                            BEGIN(LIT_STRING);
                            return TAG_CHAR;
                        }
<LIT_STRING><<EOF>>     {
                            printError("Unexpected EOF. \" expected");
                            return 0;
                        }
{NUMBER}                {
                            yylval->ident=yytext;
                            return TAG_NUMBER;
                        }
{REGULAR_STRING}        {
                            interpretRegularString(yytext,yyleng,yytext);
                            yylval->ident=yytext;
                            return TAG_REGULAR_STRING;
                        }
{LITERAL_STRING}        {
                            replaceDoubleQuoteInLiteralString(yytext,yyleng,yytext);
                            yylval->ident=yytext;
                            return TAG_LITERAL_STRING;
                        }
.                       {
                            unexpectedCharError(yytext);
                            return TAG_CHAR;
                        }
%%
#define SIZE 1024


int main(){
    FILE *file=fopen("input.txt","r");
    if (!file){
        printf("Can not open the file\n");
        return 0;
    }
    char buf[SIZE];
    char c;
    int i=0;
    while ((c=fgetc(file))!=EOF && i < SIZE){
        buf[i++]=c;
    }
    if (i == SIZE) {
        printf("File is too long!\n");
        return 1;
    }
    buf[i]='\0';
    if (ferror(file)){
        printf("An error occurred while reading a file\n");
        return 0;
    }
    fclose(file);

    int tag;
    YYSTYPE value;
    YYLTYPE coords;

    init(buf);

    do{
        tag = yylex(&value,&coords);
        if (tag>0 && tag<4){
            printf("%s ",tagNames[tag]);
            printFragment(&coords);
            printf(" : %s\n",value.ident);
        }
    }while(tag!=0);

    return 0;
}