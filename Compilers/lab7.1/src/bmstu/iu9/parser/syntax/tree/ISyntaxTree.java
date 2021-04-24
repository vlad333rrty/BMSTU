package bmstu.iu9.parser.syntax.tree;

public interface ISyntaxTree {
    ISyntaxTreeNode getRoot();

    void addChild(ISyntaxTreeNode parent,ISyntaxTreeNode child);
}
