package tokens;

import data.Fragment;

public class ArrayNameToken extends AbstractToken {
    public ArrayNameToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String toString() {
        return String.format("%s %s:%s","ARRAY",fragment,value);
    }
}
