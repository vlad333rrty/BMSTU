package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class ColonToken extends AbstractToken {
    public ColonToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "COLON";
    }
}
