package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public abstract class AbstractToken implements IToken {
    protected final String value;
    protected final Fragment fragment;

    public abstract String getTag();

    @Override
    public String toString() {
        return String.format("%s %s : %s", getTag(), fragment, value);
    }

    public String getValue() {
        return value;
    }

    protected AbstractToken(String value, Fragment fragment) {
        this.value = value;
        this.fragment = fragment;
    }
}
