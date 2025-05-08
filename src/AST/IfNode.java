//Represents an if-else control structure.
package AST;

import java.util.List;

public class IfNode extends Node {
    private final List<Node> conditions;
    private final List<List<Node>> blocks;

    public IfNode(List<Node> conditions, List<List<Node>> blocks) {
        this.conditions = conditions;
        this.blocks = blocks;
    }

    @Override
    public int evaluate(Environment env) {
        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.get(i).evaluate(env) != 0) {
                for (Node stmt : blocks.get(i)) {
                    stmt.evaluate(env);
                }
                return 0;
            }
        }
        // Optional: handle 'else' as the last block
        if (blocks.size() > conditions.size()) {
            for (Node stmt : blocks.get(blocks.size() - 1)) {
                stmt.evaluate(env);
            }
        }
        return 0;
    }
}
