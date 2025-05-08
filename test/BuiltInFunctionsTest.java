import AST.BuiltinFunctions;
import AST.Environment;
import AST.NumberNode;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class BuiltInFunctionsTest {

    @Test
    public void testAbsPositive() {
        int result = BuiltinFunctions.call("abs", List.of(new NumberNode(10)), new Environment(null, new java.util.HashMap<>()));
        assertEquals(10, result);
    }

    @Test
    public void testAbsNegative() {
        int result = BuiltinFunctions.call("abs", List.of(new NumberNode(-8)), new Environment(null, new java.util.HashMap<>()));
        assertEquals(8, result);
    }

    @Test
    public void testMax() {
        int result = BuiltinFunctions.call("max", List.of(
                new NumberNode(5),
                new NumberNode(10)
        ), new Environment(null, new java.util.HashMap<>()));
        assertEquals(10, result);
    }

    @Test
    public void testMin() {
        int result = BuiltinFunctions.call("min", List.of(
                new NumberNode(5),
                new NumberNode(10)
        ), new Environment(null, new java.util.HashMap<>()));
        assertEquals(5, result);
    }

    @Test
    public void testMinWithNegative() {
        int result = BuiltinFunctions.call("min", List.of(
                new NumberNode(-5),
                new NumberNode(0)
        ), new Environment(null, new java.util.HashMap<>()));
        assertEquals(-5, result);
    }
}
