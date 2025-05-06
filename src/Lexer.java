import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int c;
    private HashMap<Serializable,TokenType> tokenMap = new HashMap<>();
    private List<String> keywordList;
    public Lexer(String source) {
        // Constructor: takes the raw source code string as input
        // lexer is supposed to take in a .txt file
        this.source = source;
        this.keywordList = List.of("var", "function", "return", "if", "elif", "else", "while", "rum", "print", "input");
    }

    public List<Token> tokenize(String fileName) {
        // Main entry point: scans the entire input and returns a list of tokens
        try {
            // use bufferReader to read from the .txt file
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            // read character by character
            StringBuilder curr = new StringBuilder();
            boolean skipNextChar = false;
            while ((this.c = br.read()) != -1 || skipNextChar) {
                if (skipNextChar) {
                    skipNextChar = false;
                    continue;
                }
                char ch = (char) c;
                // if it's a whitespace then continue reading
                if (Character.isWhitespace(ch)) {
                    continue;
                }
                // this is the case for integer literal
                if (Character.isDigit(c)) {
                    curr.setLength(0); // make sure it's empty
                    curr.append(ch);
                    // mark the position and keep reading the next character
                    br.mark(1);
                    while ((c = br.read()) != -1) {
                        char nextCh = (char) c;
                        if (!Character.isDigit(nextCh)) {
                            br.reset();
                            skipNextChar = true;
                            break;
                        }
                        curr.append(nextCh);
                        br.mark(1);
                    }
                    tokenMap.put(curr.toString(), TokenType.INTEGER);
                    tokens.add(new Token(TokenType.INTEGER, curr.toString()));
                    continue;
                }

                // this is the case for Identifier, Keywords
                if (Character.isAlphabetic(c)) {
                    curr.setLength(0); // make sure it's empty
                    curr.append(ch);
                    // mark the position and keep reading the next character
                    br.mark(1);
                    while ((c = br.read()) != -1) {
                        char nextCh = (char) c;
                        if (!Character.isAlphabetic(nextCh)) {
                            br.reset();
                            skipNextChar = true;
                            break;
                        }
                        curr.append(nextCh);
                        br.mark(1);
                    }
                    if (!keywordList.contains(curr.toString())) {
                        tokenMap.put(curr.toString(), TokenType.IDENTIFIER);
                        tokens.add(new Token(TokenType.IDENTIFIER, curr.toString()));
                    } else {
                        keyword(curr.toString());
                    }
                }
                // Symbols and operator
                switch (c) {
                    // arithmetic
                    case '+':
                        tokenMap.put('+',TokenType.PLUS);
                        tokens.add(new Token(tokenMap.get('+'),"+"));
                        break;
                    case '-':
                        br.mark(1);
                        int nextIntComment = br.read();
                        char nextComment = (char) nextIntComment;
                        if (nextComment == '-') {
                            String comment = "--" + br.readLine();
                            tokenMap.put(comment,TokenType.COMMENT);
                            tokens.add(new Token(tokenMap.get(comment),comment));
                            break;
                        } else {
                            tokenMap.put('-',TokenType.MINUS);
                            tokens.add(new Token(tokenMap.get('-'),"-"));
                            br.reset();
                            break;
                        }
                    case '*':
                        tokenMap.put('*',TokenType.STAR);
                        tokens.add(new Token(tokenMap.get('*'),"*"));
                        break;
                    case '/':
                        tokenMap.put('/',TokenType.SLASH);
                        tokens.add(new Token(tokenMap.get('/'),"/"));
                        break;
                    case '％':
                        tokenMap.put('%',TokenType.MOD);
                        tokens.add(new Token(tokenMap.get('%'),"%"));
                        break;
                    // Logic
                    case '<':
                        br.mark(1);
                        int nextInt1 = br.read();
                        char nextChar1 = (char) nextInt1;
                        if (nextChar1 == '-') {
                            tokenMap.put("<-",TokenType.ASSIGN);
                            tokens.add(new Token(tokenMap.get("<-"),"<-"));
                            break;
                        } else if (nextChar1 == '=') {
                            tokenMap.put("<=",TokenType.LE);
                            tokens.add(new Token(tokenMap.get("<="),"<="));
                            break;
                        } else {
                            tokenMap.put('<',TokenType.LT);
                            tokens.add(new Token(tokenMap.get('<'),"<"));
                            br.reset();
                            break;
                        }
                    case '>':
                        br.mark(1);
                        int nextInt2 = br.read();
                        char nextChar2 = (char) nextInt2;
                        if (nextChar2 == '=') {
                            tokenMap.put(">=",TokenType.GE);
                            tokens.add(new Token(tokenMap.get(">="),">="));
                            break;
                        } else {
                            tokenMap.put('>',TokenType.GT);
                            tokens.add(new Token(tokenMap.get('>'),">"));
                            br.reset();
                            break;
                        }
                    case '=':
                        tokenMap.put('=',TokenType.EQ);
                        tokens.add(new Token(tokenMap.get('='),"="));
                        break;
                    case '~':
                        tokenMap.put('~',TokenType.NEQ);
                        tokens.add(new Token(tokenMap.get('~'),"~"));
                        break;
                    // Syntactical / Symbol
                    case '(':
                        tokenMap.put('(',TokenType.LPAREN);
                        tokens.add(new Token(tokenMap.get('('),"("));
                        break;
                    case ')':
                        tokenMap.put(')',TokenType.RPAREN);
                        tokens.add(new Token(tokenMap.get(')'),")"));
                        break;
                    case '{':
                        tokenMap.put('{',TokenType.LBRACE);
                        tokens.add(new Token(tokenMap.get('{'),"{"));
                        break;
                    case '}':
                        tokenMap.put('}',TokenType.RBRACE);
                        tokens.add(new Token(tokenMap.get('}'),"}"));
                        break;
                    case ',':
                        tokenMap.put(',',TokenType.COMMA);
                        tokens.add(new Token(tokenMap.get(','),","));
                        break;
                    case ';':
                        tokenMap.put(';',TokenType.SEMICOLON);
                        tokens.add(new Token(tokenMap.get(';'),";"));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens; // placeholder
    }


//    private void identifier() {
//        // Reads a full identifier or keyword, adds corresponding token
//        // reads entire sequence of characters
//        /*
//        All identifiers (variable names and functions) must consist only of lowercase letters and underscores (_).
//        this_is is a valid variable name, but thisIsNot is not.
//        */

    private void keyword(String keyword) {
        switch (keyword) {
            case "var":
                tokenMap.put("var",TokenType.VAR);
                tokens.add(new Token(TokenType.VAR, keyword));
                break;
            case "function":
                tokenMap.put("function",TokenType.FUNCTION);
                tokens.add(new Token(TokenType.FUNCTION, keyword));
                break;
            case "return":
                tokenMap.put("return",TokenType.RETURN);
                tokens.add(new Token(TokenType.RETURN, keyword));
                break;
            case "if":
                tokenMap.put("if",TokenType.IF);
                tokens.add(new Token(TokenType.IF, keyword));
                break;
            case "elif":
                tokenMap.put("elif",TokenType.ELIF);
                tokens.add(new Token(TokenType.ELIF, keyword));
                break;
            case "else":
                tokenMap.put("else",TokenType.ELSE);
                tokens.add(new Token(TokenType.ELSE, keyword));
                break;
            case "run":
                tokenMap.put("run",TokenType.RUN);
                tokens.add(new Token(TokenType.RUN, keyword));
                break;
            case "print":
                tokenMap.put("print",TokenType.PRINT);
                tokens.add(new Token(TokenType.PRINT, keyword));
                break;
            case "input":
                tokenMap.put("input",TokenType.INPUT);
                tokens.add(new Token(TokenType.INPUT, keyword));
                break;
        }
    }

//    private boolean match(char expected) {
//        // Matches the current character if it equals `expected`, and advances
//        if String.charAr
//        return false; // placeholder
//
//
//    }
//
//    public boolean hasNext() {
//        return (c != -1);
//    }

}
