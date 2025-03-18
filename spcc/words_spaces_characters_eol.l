%{
#include <stdio.h>
int characters = 0;
int words = 0;
int spaces = 0;
int eol = 0;

FILE *infile;
%}

%%

[a-zA-Z]*    { words++; characters+=yyleng; }
[ ]    { spaces++; }
[\n]        { eol++; }
.               ;          // Ignore any other characters

%%

int main(int argc, char *argv[])
{
    if (argc != 2) {
        printf("Usage: %s <input_file>\n", argv[0]);
        return 1;
    }

    // Open input file for reading
    infile = fopen(argv[1], "r");
    if (infile == NULL) {
        perror("Error opening input file");
        return 1;
    }

    yyin = infile;
    yylex();
    
    printf("Char = %d, Words = %d. Spaces = %d. EOL = %d\n", characters, words, spaces, eol);
    return 0;
}

// Define yywrap to avoid linker error
int yywrap() {
    return 1;
}
