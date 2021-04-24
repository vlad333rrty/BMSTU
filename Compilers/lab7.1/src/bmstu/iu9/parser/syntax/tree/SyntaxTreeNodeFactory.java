package bmstu.iu9.parser.syntax.tree;

import bmstu.iu9.grammar.GObject;
import bmstu.iu9.grammar.NonTerminal;
import bmstu.iu9.grammar.Terminal;

public final class SyntaxTreeNodeFactory {

    public ISyntaxTreeNode createNode(GObject gObject){
        return gObject instanceof Terminal ? new SyntaxTreeTerminalNode((Terminal) gObject) :
                new SyntaxTreeNonTerminalNode((NonTerminal) gObject);
    }
}
