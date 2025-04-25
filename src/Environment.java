//Represents the variable scope; stores variable names and their integer values.
import AST.Node;
import java.util.*;

public class Environment {
    private final Deque<Map<String, Integer>> scopes = new ArrayDeque<>();
    private final Map<String, UserFunction> functions = new HashMap<>();

    public Environment() {
        scopes.push(new HashMap<>());
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public void setVariable(String name, int value) {
        for (Map<String, Integer> scope : scopes) {
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }
        scopes.peek().put(name, value);
    }

    public int getVariable(String name) {
        for (Map<String, Integer> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        throw new RuntimeException("Variable not defined: " + name);
    }

    public void defineFunction(String name, List<String> params, List<Node> body) {
        functions.put(name, new UserFunction(params, body));
    }

    public int callFunction(String name, List<Node> args) {
        if (BuiltinFunctions.has(name)) {
            return BuiltinFunctions.call(name, args, this);
        }
        UserFunction f = functions.get(name);
        if (f == null) throw new RuntimeException("Function not defined: " + name);
        return f.invoke(args, this);
    }
}
