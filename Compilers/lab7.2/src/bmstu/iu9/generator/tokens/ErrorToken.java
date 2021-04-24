package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class ErrorToken extends AbstractToken {
    public ErrorToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "ERROR";
    }
}
