#include<stdio.h>
#include<ctype.h>
#include<string.h>
#include<stdlib.h>

#define MAX_STRING_LEN 100

typedef struct {
  char symbol;
  int address;
  char type[10];
} Symbol;

Symbol symbolsTable[MAX_STRING_LEN];
int symbolsCount = 0;
char input_expression[MAX_STRING_LEN];

const char* getType(char value){
  if(isalpha(value)){
    /*printf("%c is an alphabet\n", value);*/
    return "IDENTIFIER";
  }else if(isdigit(value)){
    /*printf("%c is an digit\n", value);*/
    return "CONSTANT";
  }else if(value == '+' || value == '-' || value == '/' || value == '*'){
    /*printf("%c is an operator\n", value);*/
    return "OPERATOR";
  }

  printf("Unknown value %c. Cannot parse the expression\n", value);
  exit(1);
}

void insert_in_symbol_table(char value){
  if(symbolsCount == MAX_STRING_LEN){
    printf("The symbol table is full. Cannot insert more symbols\n");
    return;
  }

  if(value == ' '){
    return;
  }

  // will exit the program no type found
  strcpy(symbolsTable[symbolsCount].type, getType(value));
  // for 8 digit address
  symbolsTable[symbolsCount].address = ((rand() + 10000000) % 99999999);
  symbolsTable[symbolsCount].symbol = value;
  symbolsCount++;
}

void delete_value_symbol_table(char value){
  if(symbolsCount == 0){
    printf("The symbol table is empty. Cannot delete any symbols\n");
    return;
  }
  
  int posInSymbolTable = MAX_STRING_LEN;

  for(int i = 0; i < symbolsCount; i++){
    if(value == symbolsTable[i].symbol){
      posInSymbolTable = i;
      break;
    }
  }

  if(posInSymbolTable == MAX_STRING_LEN){
    return;
  }

  for(int i = posInSymbolTable; i < symbolsCount; i++){
    // swap i with i + 1;
    Symbol temp = symbolsTable[i+1];
    symbolsTable[i+1] = symbolsTable[i];
    symbolsTable[i] = temp;
  }

  symbolsCount--;
}

void modify_value_symbol_table(char value, int index){
  if(index > 0 && index > symbolsCount){
    printf("The index is invalid. The max index is %d", symbolsCount);
  }

  strcpy(symbolsTable[index - 1].type, getType(value));
  symbolsTable[index - 1].symbol = value;
}

void print_symbol_table(){
  printf("Symbol Table: \n\n");
  printf("Number of symbols %d\n\n", symbolsCount);
  printf("ADDRESS\t| SYMBOL\t| TYPE\t\n");
  printf("----------------------------------\n");
  for (int i = 0; i < symbolsCount; i++){
    printf("%d\t| %c\t| %s\n", symbolsTable[i].address ,symbolsTable[i].symbol, symbolsTable[i].type);
  }
}

void createSymbolTable(char *input){
  // loop over each one
  for(int i = 0; input_expression[i] != '\0'; i++){
    insert_in_symbol_table(input_expression[i]);
  }
}

int main(){
  printf("Enter the expression:");
  fgets(input_expression, MAX_STRING_LEN, stdin);
  input_expression[strcspn(input_expression, "\n")] = 0; // get rid of the newline that fgets doesn't ignore
  /*printf("DEBUG: input expression is '%s'\n", input_expression);*/

  createSymbolTable(input_expression);

  int option = 1;
  char value;
  int position;

  while(option != 0){
    printf("----------------------------------\n");
    printf("----------------------------------\n");

    printf("Enter the option:\n1.Display\n2.Insert\n3.Modify\n4.Delete\n0.Exit\n");
    scanf("%d", &option);
    printf("----------------------------------\n");

    switch (option)
    {
      case 0:
        break;
      case 1:
        print_symbol_table();
        break;
      case 2:
        printf("Enter the value to insert: ");
        scanf(" %c", &value);
        /*printf("DEBUG: the value is %c\n", value);*/
        insert_in_symbol_table(value);
        break;
      case 3:
        printf("Enter the position to modify at: ");
        scanf("%d", &position);
        printf("Enter the value: ");
        scanf(" %c", &value);
        /*printf("DEBUG: value: %c ; position: %d\n", value, position);*/
        modify_value_symbol_table(value, position);
        break;
      case 4:
        printf("Enter the value to delete: ");
        scanf(" %c", &value);
        /*printf("DEBUG: the value is %c\n", value);*/
        delete_value_symbol_table(value);
        break;
      default:
        break;
    }
  }
  return 0;
}
