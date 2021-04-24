package bmstu.iu9.parser.syntax.tree;

import bmstu.iu9.grammar.GObject;

import java.util.List;

public interface ISyntaxTreeNode {
    void addChild(ISyntaxTreeNode child);

    List<ISyntaxTreeNode> getChildren();

    GObject getValue();
}
