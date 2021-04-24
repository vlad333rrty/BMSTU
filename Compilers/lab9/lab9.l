%option reentrant noyywrap bison-bridge bison-locations
%option extra-type="struct Extra *"

%{

#include <stdio.h>
#include <stdlib.h>
#include "lexer.h"
#include "lab9.tab.h"


#define YY_USER_ACTION                                 		\
	{                                                  		\
		int i;                                             	\
		struct Extra *extra = yyextra;                     	\
		if (! extra->continued) {                          	\
			yylloc->first_line = extra->cur_line;      		\
			yylloc->first_column = extra->cur_column;  		\
		}                                                  	\
		extra->continued = 0;                              	\
		for (i = 0; i < yyleng; i++)                       	\
		{                                                  	\
			if (yytext[i] == '\n')                     		\
			{                                          		\
				extra->cur_line++;                 			\
				extra->cur_column = 1;             			\
			}                                          		\
			else                                       		\
				extra->cur_column++;               			\
			}                                          		\
		                                                   	\
		yylloc->last_line = extra->cur_line;               	\
		yylloc->last_column = extra->cur_column;           	\
	}


void yyerror(YYLTYPE *loc, yyscan_t scanner, long env[26], const char *msg)
{
	printf("Error (%d,%d): %s\n", loc->first_line, loc->first_column, msg);
}

%}

LETTER   [a-zA-Z]
DIGIT    [0-9]
IDENT    {LETTER}({LETTER}|{DIGIT})*
NUMBER   {DIGIT}+

%%

[\n\t ]+

=		return ASSIGN;
\{      return LBRACE;
\}      return RBRACE;
\[      return SQ_LBRACKET;
\]      return SQ_RBRACKET;
\(      return LPAREN;
\)      return RPAREN;
,		return COMMA;
;		return SEMICOLON;
:       return COLON;
\.		return DOT;

class       return CLASS_SPEC;
final       return FINAL_SPEC;
void        return VOID_SPEC;
char        return CHAR_SPEC;
short       return SHORT_SPEC;
int         return INT_SPEC;
long        return LONG_SPEC;
float       return FLOAT_SPEC;
double      return DOUBLE_SPEC;
String      return STRING_SPEC;
private     return PRIVATE_SPEC;
protected   return PROTECTED_SPEC;
public      return PUBLIC_SPEC;
static      return STATIC_SPEC;


{IDENT}			{
					yylval->ident = strdup(yytext);
					return IDENT;
				}

{NUMBER}		{
					yylval->num = atoi(yytext);
					return NUMBER;
				}

%%

void init_scanner(const char *program, yyscan_t *scanner, struct Extra *extra)
{
	extra->continued = 0;
	extra->cur_line = 1;
	extra->cur_column = 1;
	yylex_init(scanner);
	yylex_init_extra(extra, scanner);
	yy_scan_string(program, *scanner);
}

void destroy_scanner(yyscan_t scanner)
{
	yylex_destroy(scanner);
}