package data;

import java.util.List;

public interface IMessageList {
    void addError(Position position,String message);
    void addWarning(Position position,String message);
    List<IMessage> getSorted();
}
