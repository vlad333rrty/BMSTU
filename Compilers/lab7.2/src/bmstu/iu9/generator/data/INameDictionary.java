package bmstu.iu9.generator.data;

public interface INameDictionary {
    void addName(String name);

    boolean contains(String name);

    String getName(int ind);
}
