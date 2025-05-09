// The parser transforms a list of tokens (from the lexer) into an Abstract Syntax Tree (AST).
// It supports functions, conditionals, loops, expressions, and various statements.

import AST.*;
import java.util.*;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    // Entry point to parse a full program; currently only allows top-level function definitions.
    public Node parse(Environment globalEnv) {
        Map<String, List<Node>> functions = new HashMap<>();

        while (lexer.peek() != null) {
            if (lexer.peek().equals("function")) {
                lexer.next(); // consume "function"
                String name = lexer.next();
                lexer.expect("(");
                List<String> params = new ArrayList<>();

                // Collect parameter names
                if (!lexer.peek().equals(")")) {
                    params.add(lexer.next());
                    while (lexer.match(",")) {
                        params.add(lexer.next());
                    }
                }

                lexer.expect(")");
                lexer.expect("{");
                List<Node> stmts = parseBlock();
                lexer.expect("}");

                // Store user-defined function in the environment
                Node body = new BlockNode(stmts, true);
                UserFunction fn = new UserFunction(params, List.of(body));
                globalEnv.defineFunction(name, fn);
            } else {
                throw new RuntimeException("Only function definitions allowed at top level.");
            }
        }

        return null; // Parsing done; execution begins from "entry()" elsewhere
    }

    // Parses a block of statements enclosed by braces
    public List<Node> parseBlock() {
        List<Node> block = new ArrayList<>();
        while (!Objects.equals(lexer.peek(), "}")) {
            block.add(parseStatement());
        }
        return block;
    }

    // Parses a single statement
    public Node parseStatement() {
        String token = lexer.peek();

        // Handle print statement
        if (token.equals("print")) {
            lexer.next();
            Node arg = parseExpression();
            lexer.expect(";");
            return new FunctionCallNode("print", List.of(arg));
        }

        if (token.equals("var")) return parseDeclaration();
        if (token.equals("if")) return parseIf();
        if (token.equals("while") || token.equals("run")) return parseWhile();

        // Return statement
        if (token.equals("return")) {
            lexer.next();
            Node expr = parseExpression();
            lexer.expect(";");
            return new ReturnNode(expr);
        }

        // Nested block
        if (token.equals("{")) {
            lexer.next();
            List<Node> stmts = parseBlock();
            lexer.expect("}");
            return new BlockNode(stmts, true);
        }

        // Assignment statement
        if (token.matches("[a-z_][a-z_0-9]*")) {
            String name = lexer.next();
            if (lexer.match("<-")) {
                Node expr = parseExpression();
                lexer.expect(";");
                return new AssignmentNode(name, expr);
            } else {
                lexer.moveBackPosition(); // Rewind if not an assignment
            }
        }

        // Fallback to evaluating expression as a statement
        Node expr = parseExpression();
        lexer.expect(";");
        return expr;
    }

    // Parses variable declarations
    private Node parseDeclaration() {
        lexer.expect("var");

        List<Node> declarations = new ArrayList<>();

        do {
            String name = lexer.next();
            lexer.expect("<-");
            Node expr = parseExpression();
            declarations.add(new VariableDeclarationNode(name, expr));
        } while (lexer.match(","));

        lexer.expect(";");

        // Single declaration returns the node directly, otherwise wrap in a block
        return declarations.size() == 1 ? declarations.get(0) : new BlockNode(declarations, false);
    }

    // Parses if/elif/else constructs
    private Node parseIf() {
        List<Node> conditions = new ArrayList<>();
        List<List<Node>> blocks = new ArrayList<>();

        lexer.expect("if");
        lexer.expect("(");
        conditions.add(parseExpression());
        lexer.expect(")");
        lexer.expect("{");
        blocks.add(parseBlock());
        lexer.expect("}");

        // Handle optional elif branches
        while (lexer.match("elif")) {
            lexer.expect("(");
            conditions.add(parseExpression());
            lexer.expect(")");
            lexer.expect("{");
            blocks.add(parseBlock());
            lexer.expect("}");
        }

        // Handle optional else branch
        if (lexer.match("else")) {
            lexer.expect("{");
            blocks.add(parseBlock());
            lexer.expect("}");
        }

        return new IfNode(conditions, blocks);
    }

    // Parses both `while (...) {}` and `run { ... } while (...)` loops
    private Node parseWhile() {
        boolean isRunWhile = lexer.match("run");
        if (!isRunWhile) lexer.expect("while");

        if (isRunWhile) {
            // "run { body } while (condition)"
            lexer.expect("{");
            List<Node> body = parseBlock();
            lexer.expect("}");
            lexer.expect("while");
            lexer.expect("(");
            Node condition = parseExpression();
            lexer.expect(")");
            return new WhileNode(condition, body, true);
        } else {
            // "while (condition) { body }"
            lexer.expect("(");
            Node condition = parseExpression();
            lexer.expect(")");
            lexer.expect("{");
            List<Node> body = parseBlock();
            lexer.expect("}");
            return new WhileNode(condition, body, false);
        }
    }

    // Parses binary expressions with low-precedence operators
    private Node parseExpression() {
        Node left = parseTerm();
        while (List.of("+", "-", "<", ">", "<=", ">=", "=", "~").contains(lexer.peek())) {
            String op = lexer.next();
            Node right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }

    // Parses binary expressions with medium-precedence operators
    private Node parseTerm() {
        Node left = parseFactor();
        while (List.of("*", "/", "%").contains(lexer.peek())) {
            String op = lexer.next();
            Node right = parseFactor();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }

    // Checks if a token is a number (integer)
    private boolean isNumber(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Parses basic units like numbers, variables, function calls, parentheses, unary minus
    private Node parseFactor() {
        String token = lexer.peek();

        if (token == null) throw new RuntimeException("Unexpected EOF");

        // Input expression
        if (token.equals("input")) {
            lexer.next();
            return new FunctionCallNode("input", List.of());
        }

        // Handle unary minus (e.g. -5 becomes 0 - 5)
        if ("-".equals(token)) {
            lexer.next();
            Node factor = parseFactor();
            return new BinaryOpNode(new NumberNode(0), "-", factor);
        }

        // Numeric literal
        if (isNumber(token)) {
            return new NumberNode(Integer.parseInt(lexer.next()));
        }

        // Parenthesized expression
        if (token.equals("(")) {
            lexer.next();
            Node expr = parseExpression();
            lexer.expect(")");
            return expr;
        }

        // Variable or function call
        if (token.matches("[a-z_][a-z_0-9]*")) {
            String name = lexer.next();
            if (lexer.match("(")) {
                List<Node> args = new ArrayList<>();
                if (!lexer.peek().equals(")")) {
                    args.add(parseExpression());
                    while (lexer.match(",")) {
                        args.add(parseExpression());
                    }
                }
                lexer.expect(")");
                return new FunctionCallNode(name, args);
            }
            return new VariableNode(name);
        }

        throw new RuntimeException("Unexpected token: " + token);
    }
}
