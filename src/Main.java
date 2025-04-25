//Entry point of the project; contains the main method to start the interpreter.
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }

        String code = Files.readString(Paths.get(args[0]));
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        Node program = parser.parse();

        Environment env = new Environment();
        env.callFunction("entry", List.of());
    }
}
