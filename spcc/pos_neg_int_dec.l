%{
#include <stdio.h>
int pos_int = 0;
int neg_int = 0;
int pos_dec = 0;
int neg_dec = 0;
%}

%%

-[0-9]+[./][0-9]+   { neg_dec++; }
[0-9]+[./][0-9]+    { pos_dec++; }
-[0-9]+    { neg_int++; }
[0-9]+    { pos_int++; }
.               ;          // Ignore any other characters

%%

int main()
{
    printf("Enter the text and terminate it with CTRL-d.\n");

    yylex();
    
    printf("Positive integers = %d\n, Negative integers = %d. Positive Fractions = %d. Negative Fractions = %d\n", pos_int, neg_int, pos_dec, neg_dec);
    return 0;
}

// Define yywrap to avoid linker error
int yywrap() {
    return 1;
}