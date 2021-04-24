package bmstu.iu9.data;

public class Fragment {
    private final Position start, follow;

    public Fragment(Position start, Position follow) {
        this.start = start;
        this.follow = follow;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", start.getShortRepresentation(), follow.getShortRepresentation());
    }
}
