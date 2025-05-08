//The lexer (or tokenizer); converts source code into a list of tokens.
import java.util.*;
import java.util.regex.*;

public class Lexer {
    private final List<String> tokens = new ArrayList<>();
    private int position = 0;

    private static final Pattern TOKEN_REGEX = Pattern.compile(
            "\\s*(?:(---.*)|([a-z_][a-z_0-9]*)|(\\d+)|(<-|==|~=|<=|>=|[+\\-*/%<>=~(),{};]))"
    );

    public Lexer(String code) {
        for (String line : code.split("\n")) {
            Matcher matcher = TOKEN_REGEX.matcher(line);
            while (matcher.find()) {
                if (matcher.group(1) != null) break; // Skip comment
                if (matcher.group(2) != null) tokens.add(matcher.group(2));
                else if (matcher.group(3) != null) tokens.add(matcher.group(3));
                else if (matcher.group(4) != null) tokens.add(matcher.group(4));
            }
        }
    }

    public String peek() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    public String next() {
        return position < tokens.size() ? tokens.get(position++) : null;
    }

    public boolean match(String expected) {
        if (expected.equals(peek())) {
            next();
            return true;
        }
        return false;
    }

    public void expect(String expected) {
        if (!match(expected)) {
            throw new RuntimeException("Expected token: " + expected + ", but got: " + peek());
        }
    }

    public int moveBackPosition() {
        return position--;
    }
}
