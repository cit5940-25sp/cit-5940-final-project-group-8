//Represents the variable scope; stores variable names and their integer values.
package AST;
import java.util.*;

public class Environment {
    private final Environment parent;
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, UserFunction> functions;

    public Environment(Environment parent, Map<String, UserFunction> functions) {
        this.parent = parent;
        this.functions = functions;
    }

    public void setVariable(String name, int value) {
        if (contains(name)) {
            variables.put(name, value);
        } else if(parent != null){
            parent.setVariable(name, value);
        }
        else {
            throw new RuntimeException("Assignment to undefined variable: " + name);
        }
    }

    public boolean contains(String name){
        return variables.containsKey(name);
    }

    public Map<String, UserFunction> getFunctions() {
        return functions;
    }


    public int getVariable(String name) {
        if (variables.containsKey(name)) return variables.get(name);
        if (parent != null) return parent.getVariable(name);
        throw new RuntimeException("undefined variable: " + name);
    }

    public void defineVariable(String name, int value) {
        variables.put(name, value); // 只在当前作用域定义
    }

    public void defineFunction(String name, UserFunction fn) {
        functions.put(name, fn);
    }

    public int callFunction(String name, List<Node> args) {

        UserFunction fn = functions.get(name);
        if (fn != null) {
            return fn.invoke(args, this);
        }


        if (BuiltinFunctions.has(name)) {
            return BuiltinFunctions.call(name, args, this);
        }

        throw new RuntimeException("Function not found: " + name);
    }


    public static Environment createBlockScope(Environment parent) {
        return new Environment(parent, parent.getFunctions());
    }


    public static Environment createFunctionScope(Environment caller) {
        return new Environment(null, caller.getFunctions());
    }

}

