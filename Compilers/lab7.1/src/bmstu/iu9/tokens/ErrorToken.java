package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class ErrorToken extends AbstractToken {
    public ErrorToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "ERROR";
    }
}
