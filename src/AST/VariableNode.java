//Represents a variable reference (e.g., use of a declared variable).
package AST;


public class VariableNode extends Node {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public int evaluate(Environment env) {
        return env.getVariable(name);
    }
}
