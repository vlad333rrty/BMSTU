package bmstu.iu9.tokens;

import bmstu.iu9.data.Fragment;

public class MultiplicativeOperationToken extends AbstractToken {
    public MultiplicativeOperationToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "VAL";
    }
}
