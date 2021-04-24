package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class AtToken extends AbstractToken {
    public AtToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "AT";
    }
}
