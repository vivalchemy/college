%{
/* C declarations and includes */
#include <stdio.h>
#include <string.h>

/* Counters for different token types */
int id_count = 0;
int keyword_count = 0;
int opcode_count = 0;
int special_symbol_count = 0;
int total_count = 0;

%}

/* Flex options */
%option noyywrap
%option yylineno

/* Regular expression definitions */
DIGIT       [0-9]
LETTER      [a-zA-Z]
ID          {LETTER}({LETTER}|{DIGIT})*
WHITESPACE  [ \t\n\r]

%%

 /* Keywords */
"auto"|"break"|"case"|"char"|"const"|"continue"|"default"|"do"|"double"|"else"|"enum"|"extern"|"float"|"for"|"goto"|"if"|"int"|"long"|"register"|"return"|"short"|"signed"|"sizeof"|"static"|"struct"|"switch"|"typedef"|"union"|"unsigned"|"void"|"volatile"|"while" {
    keyword_count++;
    total_count++;
}

 /* Opcodes (Operators) */
"+"|"-"|"*"|"/"|"%"|"++"|"--"|"="|"+="|"-="|"*="|"/="|"%="|"=="|"!="|">"|"<"|">="|"<="|"&&"|"||"|"!"|"&"|"|"|"^"|"~"|"<<"|">>" {
    opcode_count++;
    total_count++;
}

 /* Special Symbols */
"("|")"|"{"|"}"|"["|"]"|";"|","|"."|":"|"?"|"->"|"#" {
    special_symbol_count++;
    total_count++;
}

 /* Identifiers */
{ID} {
    /* Check if it's not already counted as a keyword */
    id_count++;
    total_count++;
}

 /* Ignore whitespace */
{WHITESPACE}+ {
    /* No action */
}

 /* Anything else */
. ;

%%

int main(int argc, char **argv) {
    if (argc > 1) {
        FILE *file = fopen(argv[1], "r");
        if (!file) {
            fprintf(stderr, "Could not open file %s\n", argv[1]);
            return 1;
        }
        yyin = file;
    } else {
        printf("Reading from standard input (type Ctrl+D to end):\n");
        yyin = stdin;
    }
    
    /* Perform lexical analysis */
    yylex();
    
    /* Print summary */
    printf("\n--- Token Count Summary ---\n");
    printf("Identifiers:    %d\n", id_count);
    printf("Keywords:       %d\n", keyword_count);
    printf("Opcodes:        %d\n", opcode_count);
    printf("Special Symbols: %d\n", special_symbol_count);
    printf("Total Tokens:   %d\n", total_count);
    
    if (argc > 1) {
        fclose(yyin);
    }
    
    return 0;
}
