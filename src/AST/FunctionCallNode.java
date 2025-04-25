//Represents a function call, such as print(x) or abs(y).
package AST;

import Environment;

import java.util.List;

public class FunctionCallNode extends Node {
    private final String functionName;
    private final List<Node> arguments;

    public FunctionCallNode(String name, List<Node> args) {
        this.functionName = name;
        this.arguments = args;
    }

    @Override
    public int evaluate(Environment env) {
        return env.callFunction(functionName, arguments);
    }
}
