import AST.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

public class ParserTest {
    @Test
    public void testParseSequentialAssignment() {
        String code = """
    {
        var x <- 1;
        x <- x + 1;
    }
    """;

        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        Environment global = new Environment(null, new HashMap<>());

        lexer.expect("{");
        List<Node> block = parser.parseBlock();
        lexer.expect("}");


        Environment local = Environment.createBlockScope(global);

        for (Node stmt : block) {
            stmt.evaluate(local);
        }


        assertEquals(2, local.getVariable("x"));
    }

    @Test
    public void testParseSimpleVariableDeclaration() {
        String code = "var x <- 5 + 2;";
        Lexer lexer = new Lexer(code);
        Environment env = new Environment(null, new HashMap<>());
        Parser parser = new Parser(lexer);

        Node stmt = parser.parseStatement();

        assertTrue(stmt instanceof VariableDeclarationNode);
        VariableDeclarationNode assign = (VariableDeclarationNode) stmt;

        int result = assign.evaluate(env);
        assertEquals(7, env.getVariable("x"));
        assertEquals(7, result);
    }

    @Test
    public void testParseIfElifElseStructure() {
        String code = """
        if (1 = 0) {
            var y <- 1;
        } elif (1 = 1) {
            var y <- 2;
        } else {
            var y <- 3;
        }
        """;

        Lexer lexer = new Lexer(code);
        Environment env = new Environment(null, new HashMap<>());
        Parser parser = new Parser(lexer);

        Node ifNode = parser.parseStatement();
        ifNode.evaluate(env);

        assertEquals(2, env.getVariable("y"));  // should take elif branch
    }

    @Test
    public void testParseWhileLoop() {
        String code = """
        {
            var x <- 0;
            while (x < 3) {
                x <- x + 1;
            }
        }
        """;

        Lexer lexer = new Lexer(code);
        Environment env = new Environment(null, new HashMap<>());
        Parser parser = new Parser(lexer);

        lexer.expect("{");                      // ✅ 现在真的是 {
        List<Node> block = parser.parseBlock(); // 解析 block
        lexer.expect("}");                      // ✅ block 结束

        for (Node stmt : block) {
            stmt.evaluate(env);
        }

        assertEquals(3, env.getVariable("x"));
    }


    @Test
    public void testFunctionDefinitionAndCall() {
        String code = """
        function add(a, b) {
            return a + b;
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);

        parser.parse(env);  // registers function in fnMap

        int result = env.callFunction("add", List.of(new NumberNode(4), new NumberNode(6)));
        assertEquals(10, result);
    }

    @Test
    public void testPrintStatementParsesAndPrints() {
        String code = """
        {
            var x <- 42;
            print x;
        }
        """;

        Lexer lexer = new Lexer(code);
        Environment env = new Environment(null, new HashMap<>());
        Parser parser = new Parser(lexer);

        lexer.expect("{");
        List<Node> block = parser.parseBlock();
        lexer.expect("}");

        // 捕获控制台输出
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        for (Node stmt : block) {
            stmt.evaluate(env);
        }

        System.setOut(System.out);  // 恢复输出

        // 检查输出
        String output = outContent.toString().trim();
        assertEquals("42", output);  // 应该打印 42
    }
    @Test
    public void testBlockScopeIsolation() {
        String code = """
        function entry() {
            {
                var x <- 5;
                print x;
            }
            print x;  --- should be undefined
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);

        parser.parse(env);

        try {
            env.callFunction("entry", List.of());
            fail("Expected RuntimeException for accessing out-of-scope variable");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("undefined variable:"));
        }
    }

    @Test
    public void testBlockScopeInheritance() {
        String code = """
        function entry() {
            var x <- 10;
            {
                var y <- x + 5;
                print y;
            }
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);

        parser.parse(env);
        env.callFunction("entry", List.of());
        // Should not throw exception, x should be visible inside block
    }

    @Test
    public void testFunctionScopeIsolationFromCaller() {
        String code = """
        function entry() {
            var outer <- 99;
            callme();
        }

        function callme() {
            print outer;  --- should be undefined
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);

        parser.parse(env);

        try {
            env.callFunction("entry", List.of());
            fail("Expected RuntimeException for accessing outer from another function");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("undefined variable"));
        }
    }

    @Test
    public void testForwardFunctionDeclaration() {
        String code = """
        function entry() {
            var result <- square(6);
            print result;
        }

        function square(x) {
            return x * x;
        }
        """;


        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);


        parser.parse(env);


        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));


        env.callFunction("entry", List.of());


        System.setOut(System.out);
        String output = out.toString().trim();

        assertEquals("36", output);
    }
    @Test
    public void testFunctionScopeParameterLocality() {
        String code = """
        function entry() {
            var a <- 5;
            add(a, 10);
        }

        function add(x, y) {
            var result <- x + y;
            print result;
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);

        parser.parse(env);
        env.callFunction("entry", List.of());

        // Should run with no errors; x and y are isolated inside add()
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));


        env.callFunction("entry", List.of());

        System.setOut(System.out);
        String output = out.toString().trim();

        assertEquals("15", output);
    }

    @Test
    public void testFunctionCallWithArguments() {
        String code = """
        function entry() {
            var x <- 1, y <- 2;
            var result <- add(x, y);
            print result;
        }

        function add(a, b) {
            return a + b;
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);


        parser.parse(env);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));


        env.callFunction("entry", List.of());


        System.setOut(System.out);


        String output = outputStream.toString().trim();
        assertEquals("3", output);  // 1 + 2 = 3
    }


    @Test
    public void testEntryWithMultipleVariableDeclarations() {
        String code = """
        function entry() {
            var x <- 1, y <- 2;
            print x;
            print y;
        }
        """;


        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment global = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);


        parser.parse(global);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));


        global.callFunction("entry", List.of());


        System.setOut(System.out);


        String[] lines = outputStream.toString().trim().split("\\R");
        assertEquals("1", lines[0]);
        assertEquals("2", lines[1]);


    }

    @Test
    public void testEntryUpdatesVariableAndPrints() {
        String code = """
        function entry() {
            var x <- 1, y <- 2;
            print x;
            y <- x * 6;
            print y;
        }
        """;

        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);


        parser.parse(env);


        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));


        env.callFunction("entry", List.of());


        System.setOut(System.out);


        String[] lines = output.toString().trim().split("\\R");
        assertEquals("1", lines[0]);
        assertEquals("6", lines[1]);
    }

    @Test
    public void testBuiltinInputReadsFromStdin() {
        String code = """
        function entry() {
            var x <- input;
            print x;
        }
        """;


        String simulatedInput = "42\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));


        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));


        Lexer lexer = new Lexer(code);
        Map<String, UserFunction> fnMap = new HashMap<>();
        Environment env = new Environment(null, fnMap);
        Parser parser = new Parser(lexer);

        parser.parse(env);
        env.callFunction("entry", List.of());

        System.setOut(System.out);


        String[] lines = output.toString().trim().split("\\R");
        assertEquals("42", lines[0]);
    }

}
