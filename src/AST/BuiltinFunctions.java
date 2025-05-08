//Contains implementations of built-in functions like print(x) and abs(x).
package AST;
import java.util.List;

public class BuiltinFunctions {
    public static boolean has(String name) {
        return List.of("abs", "max", "min", "input", "print").contains(name);
    }

    public static int call(String name, List<Node> args, Environment env) {
        //env = Environment.createBlockScope(env);
        return switch (name) {
            case "abs" -> Math.abs(args.get(0).evaluate(env));
            case "max" -> Math.max(args.get(0).evaluate(env), args.get(1).evaluate(env));
            case "min" -> Math.min(args.get(0).evaluate(env), args.get(1).evaluate(env));
            case "input" -> new java.util.Scanner(System.in).nextInt();
            case "print" -> {
                int val = args.get(0).evaluate(env);
                System.out.println(val);
                yield 0;
            }
            default -> throw new RuntimeException("Unknown built-in: " + name);
        };
    }
}
