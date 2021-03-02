package tokens;

import data.Fragment;

public abstract class AbstractToken {
    protected final String value;
    protected final Fragment fragment;

    protected AbstractToken(String value, Fragment fragment){
        this.value=value;
        this.fragment=fragment;
    }
}
