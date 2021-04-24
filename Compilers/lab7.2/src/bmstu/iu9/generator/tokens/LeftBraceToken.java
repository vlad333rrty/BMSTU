package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class LeftBraceToken extends AbstractToken {
    public LeftBraceToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "LEFT_BRACE";
    }
}
