package bmstu.iu9.data;

public interface INameDictionary {
    void addName(String name);

    boolean contains(String name);

    String getName(int ind);
}
