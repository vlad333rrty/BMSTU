package bmstu.iu9.parser.syntax.tree;

import bmstu.iu9.grammar.GObject;
import bmstu.iu9.grammar.Terminal;
import bmstu.iu9.tokens.AbstractToken;
import bmstu.iu9.tokens.EndToken;
import bmstu.iu9.tokens.IToken;

import java.util.Collections;
import java.util.List;

public class SyntaxTreeTerminalNode implements ISyntaxTreeNode{
    private final Terminal value;
    private IToken correspondingToken;

    public SyntaxTreeTerminalNode(Terminal value){
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
        String tokeVal = correspondingToken instanceof AbstractToken ? ((AbstractToken) correspondingToken).getValue()
                : EndToken.getValue();
        return String.format("%s (%s)",value.getValue(),tokeVal);
    }

    public IToken getCorrespondingToken(){
        return correspondingToken;
    }

    public void setCorrespondingToken(IToken token){
        correspondingToken = token;
    }
}
