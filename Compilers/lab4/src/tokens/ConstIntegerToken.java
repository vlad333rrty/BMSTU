package tokens;

import data.Fragment;

public class ConstIntegerToken extends AbstractToken {
    public ConstIntegerToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String toString() {
        return String.format("%s %s:%s","CONST_INT",fragment,value);
    }
}
