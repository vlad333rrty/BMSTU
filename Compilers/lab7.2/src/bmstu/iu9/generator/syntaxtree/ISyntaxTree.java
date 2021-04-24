package bmstu.iu9.generator.syntaxtree;

public interface ISyntaxTree {
    ISyntaxTreeNode getRoot();

    void addChild(ISyntaxTreeNode parent, ISyntaxTreeNode child);
}
