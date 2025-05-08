import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    @Test
    void tokenizeAssignmentLine() {
        Lexer lexer = new Lexer("var x <- 10;");
        assertEquals("var", lexer.next());
        assertEquals("x", lexer.next());
        assertEquals("<-", lexer.next());
        assertEquals("10", lexer.next());
        assertEquals(";", lexer.next());
    }

    @Test
    void skipComments() {
        Lexer lexer = new Lexer("var y <- 5; --- this is a comment");
        assertEquals("var", lexer.next());
        assertEquals("y", lexer.next());
        assertEquals("<-", lexer.next());
        assertEquals("5", lexer.next());
        assertEquals(";", lexer.next());
        assertNull(lexer.next());
    }
}
