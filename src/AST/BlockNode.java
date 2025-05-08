package AST;
import java.util.List;

public class BlockNode extends Node {
    private final List<Node> nodes;
    private final boolean scoped;

    public BlockNode(List<Node> nodes, boolean scoped) {
        this.nodes = nodes;
        this.scoped = scoped;
    }

    @Override
    public int evaluate(Environment env) {
        Environment localEnv = env;
        if(scoped)
            localEnv = Environment.createBlockScope(env);

        int result = 0;
        for (Node stmt : nodes) {
            result = stmt.evaluate(localEnv);
        }
        return result;
    }
}
