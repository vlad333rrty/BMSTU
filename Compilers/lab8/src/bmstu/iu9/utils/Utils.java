package bmstu.iu9.utils;

import bmstu.iu9.tokens.AbstractToken;
import bmstu.iu9.tokens.EndToken;
import bmstu.iu9.tokens.IToken;

public final class Utils {
    public static String getTokenValue(IToken token) {
        if (token instanceof AbstractToken) {
            return ((AbstractToken) token).getValue();
        }
        return EndToken.getValue();
    }

    public static String getTokenTag(IToken token) {
        if (token instanceof AbstractToken) {
            return ((AbstractToken) token).getTag();
        }
        return EndToken.getValue();
    }
}
