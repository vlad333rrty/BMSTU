package tokens;

import data.Fragment;

public class KeyWordToken extends AbstractToken {
    public KeyWordToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String toString() {
        return String.format("%s %s:%s","KEY_WORD",fragment,value);
    }
}
