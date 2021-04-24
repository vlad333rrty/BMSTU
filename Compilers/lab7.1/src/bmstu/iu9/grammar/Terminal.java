package bmstu.iu9.grammar;

public class Terminal extends GObject {
    public Terminal(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "Terminal: " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Terminal){
            return value.equals(((Terminal) obj).value);
        }
        return false;
    }
}
