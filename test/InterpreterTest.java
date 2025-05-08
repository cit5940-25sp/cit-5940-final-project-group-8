import AST.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class InterpreterTest {

    @Test
    public void testWhileLoopIncrementsVariable() {
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        env.defineVariable("x", 0);

        // while (x < 3) { x <- x + 1; }
        Node condition = new BinaryOpNode(new VariableNode("x"), "<", new NumberNode(3));
        Node increment = new AssignmentNode("x", new BinaryOpNode(new VariableNode("x"), "+", new NumberNode(1)));
        Node whileLoop = new WhileNode(condition, List.of(increment), false);

        whileLoop.evaluate(env);

        assertEquals(3, env.getVariable("x"));
    }

    @Test
    public void testRunWhileAlwaysRunsAtLeastOnce() {
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        env.defineVariable("x", 5);

        // run { x <- x + 1; } while (x < 0)
        Node condition = new BinaryOpNode(new VariableNode("x"), "<", new NumberNode(0));
        Node increment = new AssignmentNode("x", new BinaryOpNode(new VariableNode("x"), "+", new NumberNode(1)));
        Node runWhile = new WhileNode(condition, List.of(increment), true);

        runWhile.evaluate(env);

        assertEquals(6, env.getVariable("x"));  // should increment once
    }

    @Test
    public void testIfElseBranches() {
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        env.defineVariable("x", 10);
        env.defineVariable("y", 0);

        Node cond1 = new BinaryOpNode(new VariableNode("x"), "<", new NumberNode(5));
        Node cond2 = new BinaryOpNode(new VariableNode("x"), "=", new NumberNode(10));

        List<Node> block1 = List.of(new AssignmentNode("y", new NumberNode(100)));  // if block
        List<Node> block2 = List.of(new AssignmentNode("y", new NumberNode(200)));  // elif block
        List<Node> elseBlock = List.of(new AssignmentNode("y", new NumberNode(300))); // else block

        Node ifNode = new IfNode(List.of(cond1, cond2), List.of(block1, block2, elseBlock));
        ifNode.evaluate(env);

        assertEquals(200, env.getVariable("y"));
    }

    @Test
    public void testSimpleAssignmentAndExpressionEvaluation() {
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);


        env.defineVariable("a", 0);


        Node assign = new AssignmentNode("a", new BinaryOpNode(new NumberNode(5), "+", new NumberNode(3)));


        assign.evaluate(env);


        assertEquals(8, env.getVariable("a"));
    }


}
