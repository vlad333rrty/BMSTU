package bmstu.iu9.generator.parser;

import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.tokens.EndToken;
import bmstu.iu9.generator.tokens.IToken;

import static bmstu.iu9.utils.Utils.getTokenTag;

public class Parser extends AbstractTopDownParser {
    @Override
    protected String getTokenRepresentation(IToken token) {
        if (token instanceof EndToken) {
            return getTokenTag(token);
        }
        return "\"" + getTokenTag(token) + "\"";
    }

    @Override
    protected boolean areEqual(Terminal terminal, IToken token) {
        return terminal.getValue().equals(getTokenRepresentation(token));
    }
}
