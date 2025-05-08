//The parser; transforms the token list into an Abstract Syntax Tree (AST).
import AST.*;
import java.util.*;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Node parse(Environment globalEnv) {
        Map<String, List<Node>> functions = new HashMap<>();

        while (lexer.peek() != null) {
            if (lexer.peek().equals("function")) {
                lexer.next();
                String name = lexer.next();
                lexer.expect("(");
                List<String> params = new ArrayList<>();
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

                // Save to environment later
                Node body = new BlockNode(stmts, true);
                UserFunction fn = new UserFunction(params, List.of(body));
                globalEnv.defineFunction(name, fn);
            } else {
                throw new RuntimeException("Only function definitions allowed at top level.");
            }
        }

        return null; // entry() is called directly in Main.java
    }

    public List<Node> parseBlock() {
        List<Node> block = new ArrayList<>();
        while (!Objects.equals(lexer.peek(), "}")) {
            block.add(parseStatement());
        }
        return block;
    }

    public Node parseStatement() {
        String token = lexer.peek();

        if (token.equals("print")) {
            lexer.next();                          // consume "print"
            Node arg = parseExpression();          // no parentheses
            lexer.expect(";");                     // require semicolon
            return new FunctionCallNode("print", List.of(arg));
        }
        if (token.equals("var")) return parseDeclaration();
        if (token.equals("if")) return parseIf();
        if (token.equals("while") || token.equals("run")) return parseWhile();
        if (token.equals("return")) {
            lexer.next();
            Node expr = parseExpression();
            lexer.expect(";");
            return new ReturnNode(expr);
        }


        if (token.equals("{")) {
            lexer.next(); // consume {
            List<Node> stmts = parseBlock();
            lexer.expect("}");
            return new BlockNode(stmts, true);
        }


        if (token.matches("[a-z_][a-z_0-9]*")) {
            String name = lexer.next();
            if (lexer.match("<-")) {
                Node expr = parseExpression();
                lexer.expect(";");
                return new AssignmentNode(name, expr);
            } else {

                lexer.moveBackPosition();
            }
        }

        Node expr = parseExpression();
        lexer.expect(";");
        return expr;
    }


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

        if (declarations.size() == 1) {
            return declarations.get(0);
        } else {
            return new BlockNode(declarations, false);
        }
    }



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

        while (lexer.match("elif")) {
            lexer.expect("(");
            conditions.add(parseExpression());
            lexer.expect(")");
            lexer.expect("{");
            blocks.add(parseBlock());
            lexer.expect("}");
        }

        if (lexer.match("else")) {
            lexer.expect("{");
            blocks.add(parseBlock());
            lexer.expect("}");
        }

        return new IfNode(conditions, blocks);
    }

    private Node parseWhile() {
        boolean isRunWhile = lexer.match("run");
        if (!isRunWhile) lexer.expect("while");

        if (isRunWhile) {
            lexer.expect("{");
            List<Node> body = parseBlock();
            lexer.expect("}");
            lexer.expect("while");
            lexer.expect("(");
            Node condition = parseExpression();
            lexer.expect(")");
            return new WhileNode(condition, body, true);
        } else {
            lexer.expect("(");
            Node condition = parseExpression();
            lexer.expect(")");
            lexer.expect("{");
            List<Node> body = parseBlock();
            lexer.expect("}");
            return new WhileNode(condition, body, false);
        }
    }

    private Node parseExpression() {
        Node left = parseTerm();
        while (List.of("+", "-", "<", ">", "<=", ">=", "=", "~").contains(lexer.peek())) {
            String op = lexer.next();
            Node right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }

    private Node parseTerm() {
        Node left = parseFactor();
        while (List.of("*", "/", "%").contains(lexer.peek())) {
            String op = lexer.next();
            Node right = parseFactor();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }

    private Node parseFactor() {
        String token = lexer.peek();

        if (token == null) throw new RuntimeException("Unexpected EOF");

        if (token.equals("input")) {
            lexer.next();  // consume input
            return new FunctionCallNode("input", List.of());
        }

        if (token.matches("-?\\d+")) return new NumberNode(Integer.parseInt(lexer.next()));

        if (token.equals("(")) {
            lexer.next();
            Node expr = parseExpression();
            lexer.expect(")");
            return expr;
        }

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
