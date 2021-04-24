package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class NumberToken extends AbstractToken {
    public NumberToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "VAL";
    }
}
