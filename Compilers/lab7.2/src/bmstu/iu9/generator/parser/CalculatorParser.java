package bmstu.iu9.generator.parser;

import bmstu.iu9.generator.grammar.Terminal;
import bmstu.iu9.generator.tokens.IToken;
import bmstu.iu9.generator.tokens.NumberToken;

import static bmstu.iu9.utils.Utils.getTokenValue;

public class CalculatorParser extends AbstractTopDownParser {
    private static final String DIGIT_SYMBOL = "\"n\"";

    @Override
    protected String getTokenRepresentation(IToken token) {
        if (token instanceof NumberToken) {
            return DIGIT_SYMBOL; //todo
        }
        return getTokenValue(token);
    }

    @Override
    protected boolean areEqual(Terminal terminal, IToken token) {
        if (token instanceof NumberToken) {
            return terminal.getValue().equals(DIGIT_SYMBOL);
        }
        return terminal.getValue().equals(getTokenRepresentation(token));
    }
}
