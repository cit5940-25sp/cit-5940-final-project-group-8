//The interpreter; walks the AST and executes the program logic.
import AST.*;
import java.util.List;

public class Interpreter {

    private final Environment env;

    public Interpreter(Environment env) {
        this.env = env;
    }

    public void execute(List<Node> program) {
        for (Node node : program) {
            node.evaluate(env);
        }
    }

    public int executeFunction(String functionName, List<Node> args) {
        return env.callFunction(functionName, args);
    }

    public void runEntry() {
        env.callFunction("entry", List.of()); // no args
    }
}
