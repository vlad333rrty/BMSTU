package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class CommaToken extends AbstractToken {
    public CommaToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "COMMA";
    }
}
