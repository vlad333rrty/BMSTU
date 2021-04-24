package bmstu.iu9.generator.syntaxtree;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.NonTerminal;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTreeNonTerminalNode implements ISyntaxTreeNode {
    private final NonTerminal value;
    private final List<ISyntaxTreeNode> children = new ArrayList<>();

    public SyntaxTreeNonTerminalNode(NonTerminal value) {
        this.value = value;
    }

    @Override
    public void addChild(ISyntaxTreeNode child) {
        children.add(child);
    }

    @Override
    public List<ISyntaxTreeNode> getChildren() {
        return children;
    }

    @Override
    public GObject getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(value.getValue()).append(" -> ");
        for (ISyntaxTreeNode child : children) {
            if (child instanceof SyntaxTreeTerminalNode) {
                builder.append(child.toString()).append(" ");
            } else {
                builder.append(child.getValue().getValue()).append(" ");
            }
        }
        builder.append("\n");
        for (ISyntaxTreeNode child : children) {
            if (child instanceof SyntaxTreeNonTerminalNode) {
                builder.append(toString((SyntaxTreeNonTerminalNode) child, "\t"));
            }
        }
        return builder.toString();
    }

    private String toString(SyntaxTreeNonTerminalNode node, String space) {
        StringBuilder builder = new StringBuilder();
        builder.append(space);
        builder.append(node.value.getValue()).append(" -> ");
        for (ISyntaxTreeNode child : node.children) {
            if (child instanceof SyntaxTreeTerminalNode) {
                builder.append(child.toString()).append(" ");
            } else {
                builder.append(child.getValue().getValue()).append(" ");
            }
        }
        builder.append("\n");
        for (ISyntaxTreeNode child : node.children) {
            if (child instanceof SyntaxTreeNonTerminalNode) {
                builder.append(toString((SyntaxTreeNonTerminalNode) child, space + "\t"));
            }
        }
        return builder.toString();
    }
}
