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


        String code = Files.readString(Paths.get(args[0]));


        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);


        HashMap<String, UserFunction> functionTable = new HashMap<>();
        Environment globalEnv = new Environment(null, functionTable);


        parser.parse(globalEnv);


        globalEnv.callFunction("entry", List.of());
    }
}
