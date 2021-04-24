package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class LeftBraceToken extends AbstractToken {
    public LeftBraceToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "LEFT_BRACE";
    }
}
