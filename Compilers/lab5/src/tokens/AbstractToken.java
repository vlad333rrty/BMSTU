package tokens;

import data.Fragment;

public abstract class AbstractToken implements IToken{
    protected final String value;
    protected final Fragment fragment;

    public abstract String getTag();

    @Override
    public String toString() {
        return String.format("%s %s : %s",getTag(),fragment,value);
    }

    protected AbstractToken(String value, Fragment fragment){
        this.value=value;
        this.fragment=fragment;
    }
}
