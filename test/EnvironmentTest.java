import AST.Environment;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

public class EnvironmentTest {

    @Test
    public void testDefineAndGetVariable() {
        Environment env = new Environment(null, new HashMap<>());
        env.defineVariable("x", 42);
        assertEquals(42, env.getVariable("x"));
    }

    @Test
    public void testSetVariableInParentScope() {
        Environment parent = new Environment(null, new HashMap<>());
        parent.defineVariable("x", 5);
        assertEquals(5, parent.getVariable("x"));

        Environment child = new Environment(parent, parent.getFunctions());
        child.setVariable("x", 10);

        assertEquals(10, parent.getVariable("x"));
        assertEquals(10, child.getVariable("x"));
    }

    @Test
    public void testDefineShadowVariable() {
        Environment parent = new Environment(null, new HashMap<>());
        parent.defineVariable("x", 100);

        Environment child = new Environment(parent, parent.getFunctions());
        child.defineVariable("x", 200);

        assertEquals(100, parent.getVariable("x"));
        assertEquals(200, child.getVariable("x"));
    }

    @Test(expected = RuntimeException.class)
    public void testUndefinedVariableThrowsException() {
        Environment env = new Environment(null, new HashMap<>());
        env.getVariable("undefined");
    }

    @Test
    public void testNestedScopesCanAccessOuter() {
        Environment outer = new Environment(null, new HashMap<>());
        outer.defineVariable("a", 1);

        Environment middle = new Environment(outer, outer.getFunctions());
        Environment inner = new Environment(middle, outer.getFunctions());

        assertEquals(1, inner.getVariable("a"));
    }
}
