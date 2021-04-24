package bmstu.iu9.generator.tokens;

import bmstu.iu9.generator.data.Fragment;

public class MultiplicativeOperationToken extends AbstractToken {
    public MultiplicativeOperationToken(String value, Fragment fragment) {
        super(value, fragment);
    }

    @Override
    public String getTag() {
        return "VAL";
    }
}
