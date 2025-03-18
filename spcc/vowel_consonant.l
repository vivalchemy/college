%{
#include <stdio.h>
int vowels = 0;
int consonants = 0;
%}

%%

[aeiouAEIOU]    { vowels++; }
[a-zA-Z]        { consonants++; }
[ \t\n]         ;          // Ignore spaces, tabs, and newlines
.               ;          // Ignore any other characters

%%

int main()
{
    printf("This Lex program counts the number of vowels and ");
    printf("consonants in given text.\n");
    printf("Enter the text and terminate it with CTRL-d.\n");

    yylex();
    
    printf("Vowels = %d, consonants = %d.\n", vowels, consonants);
    return 0;
}

// Define yywrap to avoid linker error
int yywrap() {
    return 1;
}