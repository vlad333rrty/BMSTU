package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class ColonToken extends AbstractToken {
    public ColonToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "COLON";
    }
}
