package tokens;

import data.Fragment;

public class IdentToken extends AbstractToken {
    public IdentToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "IDENT";
    }
}
