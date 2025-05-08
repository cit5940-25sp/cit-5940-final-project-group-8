package AST;

public class VariableDeclarationNode extends Node {
    private final String name;
    private final Node expr;

    public VariableDeclarationNode(String name, Node expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public int evaluate(Environment env) {
        int value = expr.evaluate(env);
        env.defineVariable(name, value);
        return value;
    }
}
