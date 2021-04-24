package bmstu.iu9.generator.syntaxtree.manager;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.syntaxtree.ISyntaxTreeNode;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeNonTerminalNode;
import bmstu.iu9.generator.syntaxtree.SyntaxTreeTerminalNode;

import java.util.HashMap;
import java.util.Map;

public class SyntaxTreeManger implements ISyntaxTreeManager {
    private final Map<GObject, ISyntaxTreeNode> objectToNode = new HashMap<>();

    /**
     * Returns node corresponding the given gObject and creates it beforehand if necessary
     *
     * @param gObject
     * @return node corresponding the given gObject
     */
    @Override
    public ISyntaxTreeNode getNode(GObject gObject) {
        ISyntaxTreeNode node = objectToNode.get(gObject);
        if (node == null) {
            node = gObject instanceof Terminal ? new SyntaxTreeTerminalNode((Terminal) gObject) :
                    new SyntaxTreeNonTerminalNode((NonTerminal) gObject);
            objectToNode.put(gObject, node);
        }
        return node;
    }
}
