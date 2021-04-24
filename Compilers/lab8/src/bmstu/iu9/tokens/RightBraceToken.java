package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class RightBraceToken extends AbstractToken {
    public RightBraceToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "RIGHT_BRACE";
    }
}
