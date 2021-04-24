package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class ValueToken extends AbstractToken {
    public ValueToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "VAL";
    }
}
