package bmstu.iu9.generator.grammar;

public class NonTerminal extends GObject {
    public NonTerminal(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "NonTerminal: " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NonTerminal) {
            return value.equals(((NonTerminal) obj).value);
        }
        return false;
    }
}
