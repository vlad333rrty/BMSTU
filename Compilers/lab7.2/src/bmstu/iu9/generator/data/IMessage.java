package bmstu.iu9.generator.data;

public interface IMessage extends Comparable<IMessage> {
    boolean isError();

    String getValue();
}
