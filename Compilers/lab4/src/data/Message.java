package data;

public class Message implements IMessage{
    public enum MessageType{
        ERROR,WARNING
    }

    private final MessageType type;
    private final String value;
    private final Position position;

    public Message(MessageType type,String value,Position position){
        this.type=type;
        this.value=value;
        this.position=position;
    }

    @Override
    public boolean isError() {
        return type==MessageType.ERROR;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s",type.toString(),position,value);
    }
}
