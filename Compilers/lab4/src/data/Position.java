package data;

public class Position {
    private final int line, pos, index;

    public Position(int line,int pos,int index){
        this.line=line;
        this.pos=pos;
        this.index =index;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d,%d)",line,pos,index);
    }

    public String getShortRepresentation(){
        return String.format("(%d,%d)",line,pos);
    }
}
