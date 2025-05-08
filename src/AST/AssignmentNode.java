//Represents a variable assignment statement (e.g., x <- 5).
package AST;

public class AssignmentNode extends Node {
    private final String variable;
    private final Node expression;

    public AssignmentNode(String variable, Node expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public int evaluate(Environment env) {
        int value = expression.evaluate(env);
        env.setVariable(variable, value);
        return value;
    }
}
