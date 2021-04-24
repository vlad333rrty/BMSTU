package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class NonTerminalToken extends AbstractToken {
    public NonTerminalToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "NON_TERMINAL";
    }
}
