import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class ThreeAddressCodeGenerator {
    private static int tempCount = 1;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    generateAssignmentCode();
                    break;
                case "2":
                    generateConditionalCode();
                    break;
                case "3":
                    generateWhileCode();
                    break;
                case "4":
                    System.out.print("Enter expression (e.g., a = b + c * d): ");
                    String customExpression = scanner.nextLine();
                    if (customExpression.contains("=")) {
                        generateThreeAddressCode(customExpression);
                    } else {
                        System.out.println("Invalid expression format. Please use the format: a = b + c * d");
                    }
                    break;
                case "5":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private static void displayMenu() {
        System.out.println("\nThree-Address Code Generator");
        System.out.println("---------------------------");
        System.out.println("1. Generate code for assignment statement");
        System.out.println("2. Generate code for conditional statement");
        System.out.println("3. Generate code for while statement");
        System.out.println("4. Generate code for custom expression");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void generateAssignmentCode() {
        System.out.println("Assignment Statement Example: a = b + c * d");
        String expression = "a = b + c * d";
        generateThreeAddressCode(expression);
    }

    private static void generateConditionalCode() {
        System.out.print("Enter conditional statement (e.g., if (a < b) then c = d + e else c = d - e): ");
        String expression = scanner.nextLine();
        
        // Default to example if empty
        if (expression.trim().isEmpty()) {
            expression = "if (a < b) then c = d + e else c = d - e";
            System.out.println("Using example: " + expression);
        }
        
        try {
            // Extract condition
            int thenIndex = expression.indexOf(" then ");
            if (thenIndex == -1) throw new IllegalArgumentException("Missing 'then' keyword");
            
            String condition = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).trim();
            
            // Extract then and else parts
            int elseIndex = expression.indexOf(" else ");
            if (elseIndex == -1) throw new IllegalArgumentException("Missing 'else' keyword");
            
            String thenPart = expression.substring(thenIndex + 6, elseIndex).trim();
            String elsePart = expression.substring(elseIndex + 6).trim();
            
            // Generate labels
            String trueLabel = "L" + tempCount++;
            String falseLabel = "L" + tempCount++;
            String endLabel = "L" + tempCount++;
            
            // Generate code
            StringBuilder code = new StringBuilder();
            code.append("if ").append(condition).append(" goto ").append(trueLabel).append(";\n");
            
            // Process the else part (may need complex expression handling)
            if (elsePart.contains("=")) {
                List<String> elseCode = parseAndGenerateThreeAddressCode(elsePart);
                for (String line : elseCode) {
                    code.append(line).append("\n");
                }
            } else {
                code.append(elsePart).append(";\n");
            }
            
            code.append("goto ").append(endLabel).append(";\n");
            code.append(trueLabel).append(": ");
            
            // Process the then part (may need complex expression handling)
            if (thenPart.contains("=")) {
                List<String> thenCode = parseAndGenerateThreeAddressCode(thenPart);
                for (String line : thenCode) {
                    code.append(line).append("\n");
                }
            } else {
                code.append(thenPart).append(";\n");
            }
            
            code.append(endLabel).append(": ");
            
            System.out.println("\nThree-address code:");
            System.out.println(code.toString());
        } catch (Exception e) {
            System.out.println("Error parsing conditional statement: " + e.getMessage());
            System.out.println("Please use the format: if (condition) then statement else statement");
        }
    }

    private static void generateWhileCode() {
        System.out.print("Enter while statement (e.g., while (a < b) do c = c + d): ");
        String expression = scanner.nextLine();
        
        // Default to example if empty
        if (expression.trim().isEmpty()) {
            expression = "while (a < b) do c = c + d";
            System.out.println("Using example: " + expression);
        }
        
        try {
            // Extract condition and body
            int doIndex = expression.indexOf(" do ");
            if (doIndex == -1) throw new IllegalArgumentException("Missing 'do' keyword");
            
            String condition = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).trim();
            String body = expression.substring(doIndex + 4).trim();
            
            // Generate labels
            String loopStartLabel = "L" + tempCount++;
            String loopBodyLabel = "L" + tempCount++;
            String loopEndLabel = "L" + tempCount++;
            
            // Generate code with proper control flow
            StringBuilder code = new StringBuilder();
            code.append(loopStartLabel).append(": if ").append(condition).append(" goto ").append(loopBodyLabel).append(";\n");
            code.append("goto ").append(loopEndLabel).append(";\n");
            code.append(loopBodyLabel).append(": ");
            
            // Process the body (may need complex expression handling)
            if (body.contains("=")) {
                List<String> bodyCode = parseAndGenerateThreeAddressCode(body);
                for (String line : bodyCode) {
                    code.append(line).append("\n");
                }
            } else {
                code.append(body).append(";\n");
            }
            
            code.append("goto ").append(loopStartLabel).append(";\n");
            code.append(loopEndLabel).append(": ");
            
            System.out.println("\nThree-address code:");
            System.out.println(code.toString());
        } catch (Exception e) {
            System.out.println("Error parsing while statement: " + e.getMessage());
            System.out.println("Please use the format: while (condition) do statement");
        }
    }

    private static void generateThreeAddressCode(String expression) {
        List<String> code = parseAndGenerateThreeAddressCode(expression);
        
        System.out.println("\nThree-address code:");
        for (String line : code) {
            System.out.println(line);
        }
    }
    
    private static List<String> parseAndGenerateThreeAddressCode(String expression) {
        List<String> code = new ArrayList<>();
        
        try {
            String[] parts = expression.split("=");
            if (parts.length != 2) throw new IllegalArgumentException("Invalid assignment expression");
            
            String lhs = parts[0].trim();
            String rhs = parts[1].trim();
            
            // Use shunting yard algorithm to handle operator precedence correctly
            List<String> postfix = convertToPostfix(rhs);
            String result = evaluatePostfix(postfix, code);
            
            // Add final assignment
            code.add(lhs + " = " + result + ";");
            
        } catch (Exception e) {
            System.out.println("Error parsing expression: " + e.getMessage());
            System.out.println("Please use the format: a = b + c * d");
        }
        
        return code;
    }
    
    // Convert infix expression to postfix using Shunting Yard algorithm
    private static List<String> convertToPostfix(String infix) {
        List<String> postfix = new ArrayList<>();
        Stack<Character> operators = new Stack<>();
        
        // Tokenize the infix expression
        StringBuilder tokenBuilder = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            
            if (Character.isLetterOrDigit(c)) {
                tokenBuilder.append(c);
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (tokenBuilder.length() > 0) {
                    tokens.add(tokenBuilder.toString());
                    tokenBuilder = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else if (c == '(' || c == ')') {
                if (tokenBuilder.length() > 0) {
                    tokens.add(tokenBuilder.toString());
                    tokenBuilder = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else if (!Character.isWhitespace(c)) {
                tokenBuilder.append(c);
            } else if (tokenBuilder.length() > 0) {
                tokens.add(tokenBuilder.toString());
                tokenBuilder = new StringBuilder();
            }
        }
        
        if (tokenBuilder.length() > 0) {
            tokens.add(tokenBuilder.toString());
        }
        
        // Apply shunting yard algorithm
        for (String token : tokens) {
            if (token.length() == 1) {
                char c = token.charAt(0);
                
                if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    if (!operators.isEmpty()) operators.pop(); // Discard the '('
                } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c);
                } else {
                    postfix.add(token);
                }
            } else {
                postfix.add(token);
            }
        }
        
        while (!operators.isEmpty()) {
            postfix.add(String.valueOf(operators.pop()));
        }
        
        return postfix;
    }
    
    // Evaluate postfix expression and generate three-address code
    private static String evaluatePostfix(List<String> postfix, List<String> code) {
        Stack<String> operands = new Stack<>();
        
        for (String token : postfix) {
            if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                String operand2 = operands.pop();
                String operand1 = operands.pop();
                String temp = "t" + tempCount++;
                
                code.add(temp + " = " + operand1 + " " + token + " " + operand2 + ";");
                operands.push(temp);
            } else {
                operands.push(token);
            }
        }
        
        return operands.isEmpty() ? "" : operands.pop();
    }
    
    // Helper method to determine operator precedence
    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }
}
