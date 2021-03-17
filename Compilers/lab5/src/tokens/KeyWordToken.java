package tokens;

import data.Fragment;

public class KeyWordToken extends AbstractToken {
    public KeyWordToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "KEY_WORD";
    }
}
