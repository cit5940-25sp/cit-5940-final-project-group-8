//Represents a while loop control structure.
package AST;


import java.util.List;

public class WhileNode extends Node {
    private final Node condition;
    private final List<Node> body;
    private final boolean isRunWhile;

    public WhileNode(Node condition, List<Node> body, boolean isRunWhile) {
        this.condition = condition;
        this.body = body;
        this.isRunWhile = isRunWhile;
    }

    @Override
    public int evaluate(Environment env) {
        if (isRunWhile) {
            do {
                for (Node stmt : body) stmt.evaluate(env);
            } while (condition.evaluate(env) != 0);
        } else {
            while (condition.evaluate(env) != 0) {
                for (Node stmt : body) stmt.evaluate(env);
            }
        }
        return 0;
    }
}
