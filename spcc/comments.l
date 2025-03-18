%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

FILE *infile, *outfile;
int comment_count = 0;     // Counter for removed comments
char buffer[1000000];      // Buffer to store content temporarily
%}

%%

"//".*          { comment_count++; }  
"/"[*]([^*]*[*][^/])*[^*]*"*"[/]    { comment_count++; } 

.|\n                     { strncat(buffer, yytext, 1); }

%%

int main(int argc, char *argv[])
{
    if (argc != 3) {
        printf("Usage: %s <input_file> <output_file>\n", argv[0]);
        return 1;
    }

    // Open input file for reading
    infile = fopen(argv[1], "r");
    if (infile == NULL) {
        perror("Error opening input file");
        return 1;
    }

    // Open output file for writing
    outfile = fopen(argv[2], "w");
    if (outfile == NULL) {
        perror("Error opening output file");
        fclose(infile);
        return 1;
    }

    // Redirect Lex input stream to the input file
    yyin = infile;

    // Clear buffer and run the lexer
    buffer[0] = '\0';
    yylex();

    // Write the comment count at the top of the output file
    fprintf(outfile, "/* Comments removed: %d */\n", comment_count);

    // Write the cleaned content
    fprintf(outfile, "%s", buffer);

    // Close the files
    fclose(infile);
    fclose(outfile);

    printf("Comments removed: %d\n", comment_count);
    printf("Output written to %s\n", argv[2]);
    
    return 0;
}

// Define yywrap to avoid linker error
int yywrap() {
    return 1;
}