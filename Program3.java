import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.EmptyStackException;

//sebastian rodriguez stacks

public class Program3 {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Program3.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("Program3.out"));

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String sanitizedLine = line.replaceAll("\\s", ""); // Remove whitespace
                    String postfix = InfixToPostfix(sanitizedLine);
                    writer.write(line + " -> " + postfix + "\n");
                } catch (InvalidExpressionException e) {
                    writer.write(line + " -> " + e.getMessage() + "\n");
                } catch (EmptyStackException e) {
                    writer.write(line + " -> unmatched parens\n");
                }
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String InfixToPostfix(String s) throws InvalidExpressionException {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();
        boolean prevIsOperand = false;

        for (char c : s.toCharArray()) {
            if (isOperand(c)) {
                if (prevIsOperand) {
                    postfix.append(c); // Append to the current operand
                } else {
                    postfix.append(" ").append(c); // Start a new operand
                }
                prevIsOperand = true;
            } else if (c == '(') {
                operatorStack.push(c);
                prevIsOperand = false;
            } else if (c == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    postfix.append(" ").append(operatorStack.pop());
                }
                if (!operatorStack.isEmpty() && operatorStack.peek() == '(') {
                    operatorStack.pop(); // Pop '('
                } else {
                    throw new InvalidExpressionException("unmatched parens");
                }
                prevIsOperand = false;
            } else if (isOperator(c)) {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(' &&
                        getPrecedence(c) <= getPrecedence(operatorStack.peek())) {
                    postfix.append(" ").append(operatorStack.pop());
                }
                operatorStack.push(c);
                prevIsOperand = false;
            } else {
                throw new InvalidExpressionException("invalid character");
            }
        }

        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek() == '(') {
                throw new InvalidExpressionException("unmatched parens");
            }
            postfix.append(" ").append(operatorStack.pop());
        }

        return postfix.toString().trim(); 
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private static boolean isOperand(char c) {
        return Character.isDigit(c) || c == '.' || c == '(' || c == ')' || c == '\u2013' || c == '-';
    }

    private static int getPrecedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/' || operator == '%') {
            return 2;
        }
        return 0; 
    }
}

class InvalidExpressionException extends Exception {
    public InvalidExpressionException(String message) {
        super(message);
    }
}

