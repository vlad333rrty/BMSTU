package bmstu.iu9.grammar;

import java.io.Serializable;
import java.util.Objects;

public abstract class GObject implements Serializable {
    protected String value;

    public String getValue(){
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    protected GObject(String value) {
        this.value = value;
    }
}
