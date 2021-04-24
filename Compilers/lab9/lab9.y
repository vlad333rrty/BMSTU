%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "lexer.h"

#define MEM(size) ((char *)malloc( (size + 1) * sizeof(char)));
#define INDENT 4


char * get_indent(unsigned indent) {
    char *res = (char *)malloc(indent + 1);
    for (long i = 0; i < indent; i++) {
        res[i] = ' ';
    }
    res[indent] = '\0';
    return res;
}


%}

%define api.pure
%locations
%lex-param {yyscan_t scanner}
%parse-param {yyscan_t scanner}
%parse-param { long env[26]}

%union {
	char* ident;
	long num;
}

%token <ident> IDENT
%token <num> NUMBER
%token SQ_LBRACKET SQ_RBRACKET COMMA SEMICOLON COLON LPAREN RPAREN LBRACE RBRACE
%token ASSIGN DOT
%token VOID_SPEC
%token FINAL_SPEC CHAR_SPEC SHORT_SPEC INT_SPEC LONG_SPEC FLOAT_SPEC DOUBLE_SPEC STRING_SPEC PRIVATE_SPEC PROTECTED_SPEC PUBLIC_SPEC
%token CLASS_SPEC STATIC_SPEC

%type <ident> start class_decl class_entry field_decl field_mods void_mods access_mod type method_decl params ident_val constructor

%{
int yylex(YYSTYPE * yylval_param, YYLTYPE * yylloc_param , yyscan_t scanner);
void yyerror(YYLTYPE *yylloc, yyscan_t scanner, long env[26], const char *msg);
%}

%%

start:
	class_decl add_indent class_entry remove_indent					{
											printf("%s\n%s",$1,$3);
											}
	;
class_decl:
	access_mod CLASS_SPEC ident_val LBRACE						{
											char* indent = get_indent(env[0]);
											$$ = MEM(strlen(indent) + strlen($1) + strlen("class") + strlen($3) + 1 + 2);
											sprintf($$,"%s%s class %s{",indent,$1,$3);
											free(indent);
											}
	;

class_entry:
	field_decl class_entry								{
											char* indent = get_indent(env[0]);
											$$ = MEM(strlen(indent) +strlen($1) + 1 + strlen($2));
											sprintf($$,"%s%s\n%s",indent,$1,$2);
											free(indent);
											}
	|
	method_decl class_entry									{
												char* indent = get_indent(env[0]);
												$$ = MEM(strlen(indent) +strlen($1) + 1 + strlen($2));
												sprintf($$,"%s%s\n%s",indent,$1,$2);
												free(indent);
												}
	|
	class_decl add_indent class_entry remove_indent class_entry 				{
												char* indent = get_indent(env[0] - INDENT);
												$$ = MEM(strlen(indent) + strlen($1) + 1 + strlen($3) + 1 + strlen($5));
												sprintf($$,"%s%s\n%s\n%s",indent,$1,$3,$5);
												free(indent);
												}
	|
	constructor class_entry									{
												char* indent = get_indent(env[0]);
												$$ = MEM(strlen(indent)+strlen($1) + 1 + strlen($2));
												sprintf($$,"%s%s\n%s",indent,$1,$2);
												free(indent);
	 											}
	|
	RBRACE							{
									char* indent = get_indent(env[0] - INDENT);
									$$ = MEM(strlen(indent) + strlen("}"));
									sprintf($$,"%s}",indent);
									free(indent);
								}
	;

field_decl:
	field_mods ident_val SEMICOLON			{
									$$ = MEM(strlen($1) + strlen($2) + 1 + 1);
									sprintf($$, "%s %s;", $1, $2);
                                                                }
       	;

field_mods:
	access_mod STATIC_SPEC FINAL_SPEC type			{
									if (strlen($1)>0){
										$$ = MEM(strlen($1) + strlen("static") + strlen("final")+ strlen($4) + 3);
										sprintf($$, "%s static final %s", $1, $4);
									}else{
										$$ = MEM(strlen($1) + strlen("static") + strlen("final")+ strlen($4) + 3);
										sprintf($$, "%s static final %s", $1, $4);
									}
								}
	|
	access_mod FINAL_SPEC type				{
									if (strlen($1)>0){
										$$ = MEM(strlen($1) + strlen("final") + strlen($3) + 2);
										sprintf($$, "%s final %s", $1, $3);
									}else{
										$$ = MEM(strlen("final")+strlen($3) + 1);
										sprintf($$,"final %s",$3);
									}
								}
	|
	access_mod STATIC_SPEC type				{
									if (strlen($1)>0){
										$$ = MEM(strlen($1) + strlen("static") + strlen($3) + 2);
										sprintf($$, "%s static %s", $1, $3);
									}else{
										$$ = MEM(strlen("static") + strlen($3) + 1);
										sprintf($$,"static %s",$3);
									}
								}
	|
	access_mod type						{
									if (strlen($1)>0){
										$$ = MEM(strlen($1) + strlen($2) + 1);
										sprintf($$, "%s %s", $1, $2);
									}else{
										$$ = MEM(strlen($2));
										sprintf($$,"%s",$2);
									}
								}
       	;

void_mods:
	access_mod STATIC_SPEC FINAL_SPEC VOID_SPEC			{
										if (strlen($1)>0){
											$$ = MEM(strlen($1) + strlen("static") + strlen("final")+ strlen("void") + 3);
											sprintf($$, "%s static final void", $1);
										}else{
											$$ = MEM(strlen("static") + strlen("final")+ strlen("void") + 2);
											sprintf($$, "static final void");
										}
        								}
        |
       	access_mod FINAL_SPEC VOID_SPEC					{
       										if (strlen($1)>0){
       											$$ = MEM(strlen($1) + strlen("final") + strlen("void") + 2);
       											sprintf($$, "%s final void", $1);
       										}else{
       											$$ = MEM(strlen("final") + strlen("void") + 1);
       											sprintf($$, "final void");
       										}
        								}
        |
        access_mod STATIC_SPEC VOID_SPEC				{
										if (strlen($1)>0){
       											$$ = MEM(strlen($1) + strlen("static") + strlen("void") + 2);
       											sprintf($$, "%s static void", $1);
       										}else{
       											$$ = MEM(strlen("final") + strlen("void") + 1);
       											sprintf($$, "static void");
       										}
        								}
        |
        access_mod VOID_SPEC						{
        									if (strlen($1)>0){
        										$$ = MEM(strlen($1) + strlen("void") + 1);
        										sprintf($$, "%s void", $1);
        									}else{
        										$$ = MEM(strlen("void"));
        										sprintf($$,"void");
        									}
        								}
        ;

access_mod:
	PRIVATE_SPEC						{
								$$ = MEM(strlen("private"));
                                                                 sprintf($$, "private");
                                                                 }
	|
	%empty							{$$ ="";}
	|
	PROTECTED_SPEC						{$$ = MEM(strlen("protected"));
                                                                sprintf($$, "protected");}
	|
	PUBLIC_SPEC						{$$ = MEM(strlen("public"));
                                                               	sprintf($$, "public");}
	;
type:
	 CHAR_SPEC                                                                   {
                                                                                            $$ = MEM(strlen("char"));
                                                                                            sprintf($$, "char");
                                                                                        }
         | SHORT_SPEC                                                                {   $$ = MEM(strlen("short"));
                                                                                            sprintf($$, "short");
                                                                                        }
         | INT_SPEC                                                                  {   $$ = MEM(strlen("int"));
                                                                                            sprintf($$, "int");
                                                                                        }
         | LONG_SPEC                                                                 {   $$ = MEM(strlen("long"));
                                                                                            sprintf($$, "long");
                                                                                        }
         | FLOAT_SPEC                                                                {   $$ = MEM(strlen("float"));
                                                                                            sprintf($$, "float");
                                                                                        }
         | DOUBLE_SPEC                  {   $$ = MEM(strlen("double"));
                                                                                            sprintf($$, "double");
                                                                                        }
         |
         STRING_SPEC			{$$ = MEM(strlen("String"));
                                         sprintf($$, "String");}

         ;

method_decl:
	field_mods ident_val LPAREN params RPAREN LBRACE RBRACE			{
												$$ = MEM(strlen($1) + strlen($2) + strlen($4) + 4 + 1);
                                                                                                sprintf($$, "%s %s(%s){}",$1,$2,$4);
										}
	|
	void_mods ident_val LPAREN params RPAREN LBRACE RBRACE			{
											$$ = MEM(strlen($1) + strlen($2) + strlen($4) + 4 + 1);
											sprintf($$, "%s %s(%s){}",$1,$2,$4);
										}
	;
constructor:
	access_mod ident_val LPAREN params RPAREN LBRACE RBRACE				{
												$$ = MEM(strlen($1) + strlen($2) + strlen($4) + 3);
												sprintf($$,"%s %s(%s){}",$1,$2,$4);
											}
	;
params:
	type ident_val params		{$$ = MEM(strlen($1) + strlen($2) + strlen($3) + 2);
					sprintf($$, "%s %s %s",$1,$2,$3);}
	|
	type ident_val			{$$ = MEM(strlen($1) + strlen($2) + 1);
                                         sprintf($$, "%s %s",$1,$2);}
        |
        %empty				{$$ = "";}
	;
ident_val:
	IDENT				{
						$$ = MEM(strlen(yylval.ident));
						sprintf($$,"%s",yylval.ident);
					}
	;
add_indent:
	%empty	                                                          {   env[0]+=INDENT; }
    ;
remove_indent:
	%empty                                                    {   env[0]-=INDENT; }
    ;
%%

int main()
{
	yyscan_t scanner;
	struct Extra extra;
	long env[26];
    env[0] = 0;
    char * buffer = 0;
    long length;
    FILE * f = fopen ("test.java", "rb");

    if (f) {
    	fseek (f, 0, SEEK_END);
    	length = ftell (f);
    	fseek (f, 0, SEEK_SET);
    	buffer = malloc (length);
    	if (buffer)
    		fread (buffer, 1, length, f);
      fclose (f);
   	}
	init_scanner(buffer, &scanner, &extra);
	yyparse(scanner, env);
	destroy_scanner(scanner);
    free(buffer);
	return 0;
}