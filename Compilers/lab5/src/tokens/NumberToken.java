package tokens;

import data.Fragment;

public class NumberToken extends AbstractToken {
    public NumberToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "NUMBER";
    }
}
