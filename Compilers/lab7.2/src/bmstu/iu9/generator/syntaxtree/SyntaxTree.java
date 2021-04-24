package bmstu.iu9.generator.syntaxtree;

public class SyntaxTree implements ISyntaxTree {
    private final ISyntaxTreeNode root;

    public SyntaxTree(ISyntaxTreeNode root) {
        this.root = root;
    }

    @Override
    public ISyntaxTreeNode getRoot() {
        return root;
    }

    @Override
    public void addChild(ISyntaxTreeNode parent, ISyntaxTreeNode child) {
        parent.addChild(child);
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
