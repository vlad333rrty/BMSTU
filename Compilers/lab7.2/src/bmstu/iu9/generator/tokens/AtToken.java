package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class AtToken extends AbstractToken {
    public AtToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "AT";
    }
}
