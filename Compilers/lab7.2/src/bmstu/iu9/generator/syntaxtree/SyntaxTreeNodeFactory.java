package bmstu.iu9.generator.syntaxtree;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.tokens.IToken;

public final class SyntaxTreeNodeFactory {

    public ISyntaxTreeNode createNode(GObject gObject) {
        return gObject instanceof Terminal ? new SyntaxTreeTerminalNode((Terminal) gObject) :
                new SyntaxTreeNonTerminalNode((NonTerminal) gObject);
    }

    public SyntaxTreeTerminalNode createTerminalNode(Terminal terminal, IToken token) {
        SyntaxTreeTerminalNode node = new SyntaxTreeTerminalNode(terminal);
        node.setCorrespondingToken(token);
        return node;
    }

    public SyntaxTreeNonTerminalNode createNonTerminalNode(NonTerminal nonTerminal) {
        return new SyntaxTreeNonTerminalNode(nonTerminal);
    }
}
