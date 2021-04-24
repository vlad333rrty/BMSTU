package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class CommaToken extends AbstractToken {
    public CommaToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "COMMA";
    }
}
