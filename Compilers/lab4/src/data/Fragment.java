package data;

public class Fragment {
    private final int line,start,end;

    public Fragment(int line, int start, int end) {
        this.line = line;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d) - (%d,%d)",line,start,line,end);
    }
}
