package bmstu.iu9.parser.syntax.tree.manager;

import bmstu.iu9.grammar.GObject;
import bmstu.iu9.parser.syntax.tree.ISyntaxTreeNode;

public interface ISyntaxTreeManager {
    ISyntaxTreeNode getNode(GObject gObject);
}
