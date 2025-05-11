import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import AST.*;
public class SPROLARunner {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }

        // Read the contents of the source file into a single string
        String code = Files.readString(Paths.get(args[0]));

        // Create a Lexer to tokenize the input source code
        Lexer lexer = new Lexer(code);
        
        // Create a Parser to build the AST (Abstract Syntax Tree) from tokens
        Parser parser = new Parser(lexer);

        // Initialize a function table to store user-defined functions
        HashMap<String, UserFunction> functionTable = new HashMap<>();
        Environment globalEnv = new Environment(null, functionTable);


        parser.parse(globalEnv);


        globalEnv.callFunction("entry", List.of());
    }
}
