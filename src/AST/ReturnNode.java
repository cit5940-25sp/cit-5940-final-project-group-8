package AST;

public class ReturnNode extends Node {
    private final Node expr;

    public ReturnNode(Node expr) {
        this.expr = expr;
    }

    @Override
    public int evaluate(Environment env) {
        return expr.evaluate(env);
    }
}
