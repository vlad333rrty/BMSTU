package bmstu.iu9.generator.syntaxtree;

import bmstu.iu9.generator.grammar.GObject;

import java.util.List;

public interface ISyntaxTreeNode {
    void addChild(ISyntaxTreeNode child);

    List<ISyntaxTreeNode> getChildren();

    GObject getValue();
}
