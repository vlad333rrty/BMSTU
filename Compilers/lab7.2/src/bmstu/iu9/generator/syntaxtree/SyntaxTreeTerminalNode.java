package bmstu.iu9.generator.syntaxtree;

import bmstu.iu9.generator.grammar.GObject;
import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.tokens.IToken;

import java.util.Collections;
import java.util.List;

public class SyntaxTreeTerminalNode implements ISyntaxTreeNode {
    private final Terminal value;
    private IToken correspondingToken;

    public SyntaxTreeTerminalNode(Terminal value) {
        this.value = value;
    }

    @Override
    public void addChild(ISyntaxTreeNode child) {
        throw new UnsupportedOperationException("Terminal node can't have children");
    }

    @Override
    public List<ISyntaxTreeNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public GObject getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("(%s)", correspondingToken);
    }

    public IToken getCorrespondingToken() {
        return correspondingToken;
    }

    public void setCorrespondingToken(IToken token) {
        correspondingToken = token;
    }
}
