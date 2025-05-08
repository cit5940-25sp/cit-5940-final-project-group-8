package AST;
import java.util.*;

public class UserFunction {
    private final List<String> parameters;
    private final List<Node> body;

    public UserFunction(List<String> params, List<Node> body) {
        this.parameters = params;
        this.body = body;
    }

    public int invoke(List<Node> args, Environment outerEnv) {
        if (args.size() != parameters.size())
            throw new RuntimeException("Argument count mismatch");

        Environment local = Environment.createFunctionScope(outerEnv);
        // call with parameter
        for (int i = 0; i < args.size(); i++) {
            int value = args.get(i).evaluate(outerEnv);
            local.defineVariable(parameters.get(i), value);
        }

        int result = 0;
        for (Node stmt : body) {
            if (stmt instanceof ReturnNode ret) {
                return ret.evaluate(local);
            } else {
                result = stmt.evaluate(local);
            }
        }
        return result;
    }
}
