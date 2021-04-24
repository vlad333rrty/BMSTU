package bmstu.iu9.generator.syntaxtree.manager;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.syntaxtree.ISyntaxTreeNode;

public interface ISyntaxTreeManager {
    ISyntaxTreeNode getNode(GObject gObject);
}
