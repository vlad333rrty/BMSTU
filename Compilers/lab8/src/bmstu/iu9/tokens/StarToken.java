package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class StarToken extends AbstractToken {
    public StarToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "STAR";
    }
}
