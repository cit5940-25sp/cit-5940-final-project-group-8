//Represents an integer constant in the AST.
package AST;

import Environment;

public class NumberNode extends Node {
    private final int value;

    public NumberNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate(Environment env) {
        return value;
    }
}
