package bmstu.iu9.data;

public interface IMessage extends Comparable<IMessage>{
    boolean isError();
    String getValue();
}
