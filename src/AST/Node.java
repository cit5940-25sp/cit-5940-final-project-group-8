//An abstract class or interface; the parent type of all AST nodes.
package AST;

public abstract class Node {
    public abstract int evaluate(Environment env);
}
