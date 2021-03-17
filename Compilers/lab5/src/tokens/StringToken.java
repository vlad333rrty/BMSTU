package tokens;

import data.Fragment;

public class StringToken extends AbstractToken{
    public StringToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "STRING";
    }
}
