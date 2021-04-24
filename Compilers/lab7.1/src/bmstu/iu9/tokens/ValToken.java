package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class ValToken extends AbstractToken {
    public ValToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "VAL";
    }
}
