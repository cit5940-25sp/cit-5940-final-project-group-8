//Represents a binary operation node (e.g., a + b).
//Represents a binary operation node (e.g., a + b).
package AST;

import Environment;

public class BinaryOpNode extends Node {
    private final Node left, right;
    private final String operator;

    public BinaryOpNode(Node left, String operator, Node right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public int evaluate(Environment env) {
        int lval = left.evaluate(env);
        int rval = right.evaluate(env);

        return switch (operator) {
            case "+" -> lval + rval;
            case "-" -> lval - rval;
            case "*" -> lval * rval;
            case "/" -> rval == 0 ? 0 : lval / rval;
            case "%" -> lval % rval;
            case "<" -> lval < rval ? 1 : 0;
            case ">" -> lval > rval ? 1 : 0;
            case "<=" -> lval <= rval ? 1 : 0;
            case ">=" -> lval >= rval ? 1 : 0;
            case "=" -> lval == rval ? 1 : 0;
            case "~" -> lval != rval ? 1 : 0;
            default -> throw new RuntimeException("Unknown operator: " + operator);
        };
    }
}
