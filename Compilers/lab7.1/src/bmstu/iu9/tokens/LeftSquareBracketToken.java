package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class LeftSquareBracketToken extends AbstractToken {
    public LeftSquareBracketToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "LEFT_SQUARE_BRACKET";
    }
}
