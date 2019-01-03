package cc.blynk.server.exp4j.shuntingyard;

import cc.blynk.server.exp4j.function.Function;
import cc.blynk.server.exp4j.operator.Operator;
import cc.blynk.server.exp4j.tokenizer.FunctionToken;
import cc.blynk.server.exp4j.tokenizer.OperatorToken;
import cc.blynk.server.exp4j.tokenizer.Token;
import cc.blynk.server.exp4j.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Shunting yard implementation to convert infix to reverse polish notation
 */
public final class ShuntingYard {

    private ShuntingYard() {
    }

    /**
     * Convert a Set of tokens from infix to reverse polish notation
     * @param expression the expression to convert
     * @param userFunctions the custom functions used
     * @param userOperators the custom operators used
     * @param variableNames the variable names used in the expression
     * @param implicitMultiplication set to fasle to turn off implicit multiplication
     */
    public static Token[] convertToRPN(String expression,
                                       Map<String, Function> userFunctions,
                                       Map<String, Operator> userOperators,
                                       Set<String> variableNames,
                                       boolean implicitMultiplication) {
        Stack<Token> stack = new Stack<>();
        List<Token> output = new ArrayList<>();
        Stack<FunctionToken> functionTokenStack = new Stack<>();

        Tokenizer tokenizer = new Tokenizer(expression,
                userFunctions, userOperators, variableNames, implicitMultiplication);

        while (tokenizer.hasNext()) {
            Token token = tokenizer.nextToken();
            switch (token.getType()) {
            case Token.TOKEN_NUMBER :
            case Token.TOKEN_VARIABLE :
                output.add(token);
                break;
            case Token.TOKEN_FUNCTION :
                functionTokenStack.add((FunctionToken) token);
                stack.add(token);
                break;
            case Token.TOKEN_SEPARATOR :
                if (!functionTokenStack.empty()) {
                    functionTokenStack.peek().incrArgumentsCounter();
                }
                while (!stack.empty() && stack.peek().getType() != Token.TOKEN_PARENTHESES_OPEN) {
                    output.add(stack.pop());
                }
                if (stack.empty() || stack.peek().getType() != Token.TOKEN_PARENTHESES_OPEN) {
                    throw new IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses");
                }
                break;
            case Token.TOKEN_OPERATOR:
                while (!stack.empty() && stack.peek().getType() == Token.TOKEN_OPERATOR) {
                    OperatorToken o1 = (OperatorToken) token;
                    OperatorToken o2 = (OperatorToken) stack.peek();
                    if (o1.getOperator().getNumOperands() == 1 && o2.getOperator().getNumOperands() == 2) {
                        break;
                    } else if ((o1.getOperator().isLeftAssociative()
                            && o1.getOperator().getPrecedence() <= o2.getOperator().getPrecedence())
                            || (o1.getOperator().getPrecedence() < o2.getOperator().getPrecedence())) {
                        output.add(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(token);
                break;
            case Token.TOKEN_PARENTHESES_OPEN :
                stack.push(token);
                break;
            case Token.TOKEN_PARENTHESES_CLOSE :
                while (stack.peek().getType() != Token.TOKEN_PARENTHESES_OPEN) {
                    output.add(stack.pop());
                }
                stack.pop();
                if (!stack.isEmpty() && stack.peek().getType() == Token.TOKEN_FUNCTION) {
                    functionTokenStack.pop();
                    output.add(stack.pop());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Token type encountered. This should not happen");
            }
        }
        while (!stack.empty()) {
            Token t = stack.pop();
            if (t.getType() == Token.TOKEN_PARENTHESES_CLOSE || t.getType() == Token.TOKEN_PARENTHESES_OPEN) {
                throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression");
            } else {
                output.add(t);
            }
        }

        return output.toArray(new Token[0]);
    }
}
