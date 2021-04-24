package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class RightSquareBracketToken extends AbstractToken {
    public RightSquareBracketToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "RIGHT_SQUARE_BRACKET";
    }
}
