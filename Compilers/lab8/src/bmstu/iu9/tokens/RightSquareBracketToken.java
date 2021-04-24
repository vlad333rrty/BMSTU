package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class RightSquareBracketToken extends AbstractToken {
    public RightSquareBracketToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "RIGHT_SQUARE_BRACKET";
    }
}
