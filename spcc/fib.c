#include <stdio.h>

/**
 * Program to calculate Fibonacci sequence
 * The Fibonacci sequence is a series of numbers where each number
 * is the sum of the two preceding ones, starting from 0 and 1.
 */

int main() {
    int n, i;
    // First two Fibonacci numbers
    int first = 0;
    int second = 1;
    int next; // To store the next Fibonacci number
    
    // Get user input
    printf("Enter the number of Fibonacci terms to generate: ");
    scanf("%d", &n);
    
    // Input validation
    if (n <= 0) {
        printf("Please enter a positive integer.\n");
        return 1; // Exit with error code
    }
    
    // Print header
    printf("\nFibonacci Sequence:\n");
    
    // Special handling for first term
    if (n >= 1) {
        printf("%d", first);
    }
    
    // Special handling for second term
    if (n >= 2) {
        printf(", %d", second);
    }
    
    // Generate the remaining Fibonacci numbers
    for (i = 3; i <= n; i++) {
        // Calculate the next number in sequence
        next = first + second;
        
        // Print the next number
        printf(", %d", next);
        
        // Update values for next iteration
        first = second;
        second = next;
    }
    
    printf("\n");
    return 0; // Exit successfully
}
