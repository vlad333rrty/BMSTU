package tokens;

import data.Fragment;

public class OperationToken extends AbstractToken{
    public OperationToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "OPERATION";
    }
}
