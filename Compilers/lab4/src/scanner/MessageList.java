package scanner;

import data.IMessage;
import data.IMessageList;
import data.Message;
import data.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MessageList implements IMessageList {
    private final List<IMessage> messages=new ArrayList<>();

    @Override
    public void addError(Position position, String message) {
        messages.add(new Message(Message.MessageType.ERROR,message,position));
    }

    @Override
    public void addWarning(Position position, String message) {
        messages.add(new Message(Message.MessageType.WARNING,message,position));
    }

    @Override
    public List<IMessage> getSorted() {
        List<IMessage> sorted=messages;
        sorted.sort(Comparator.comparing(IMessage::getValue));
        return sorted;
    }
}
