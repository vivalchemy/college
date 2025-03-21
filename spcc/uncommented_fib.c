/* Comments removed: 14 */
#include <stdio.h>



int main() {
    int n, i;
    
    int first = 0;
    int second = 1;
    int next; 
    
    
    printf("Enter the number of Fibonacci terms to generate: ");
    scanf("%d", &n);
    
    
    if (n <= 0) {
        printf("Please enter a positive integer.\n");
        return 1; 
    }
    
    
    printf("\nFibonacci Sequence:\n");
    
    
    if (n >= 1) {
        printf("%d", first);
    }
    
    
    if (n >= 2) {
        printf(", %d", second);
    }
    
    
    for (i = 3; i <= n; i++) {
        
        next = first + second;
        
        
        printf(", %d", next);
        
        
        first = second;
        second = next;
    }
    
    printf("\n");
    return 0; 
}
