package data;

import java.util.Objects;

public class Position implements Comparable<Position>{
    private int line, pos, index;

    public Position(int line,int pos,int index){
        this.line=line;
        this.pos=pos;
        this.index =index;
    }

    public Position(Position position){
        this.pos=position.pos;
        this.index=position.index;
        this.line=position.line;
    }

    public void incLine(){
        line++;
    }

    public void incPos(){
        pos++;
    }

    public void incIndex(){
        index++;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public int getIndex() {
        return index;
    }

    public int goToNextSymbol(){
        pos++;
        return index++;
    }

    public void setPos(int pos){
        this.pos=pos;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d,%d)",line,pos,index);
    }

    public String getShortRepresentation(){
        return String.format("(%d,%d)",line,pos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return line == position.line && pos == position.pos && index == position.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, pos, index);
    }

    @Override
    public int compareTo(Position o) {
        return Integer.compare(index, o.index);
    }
}
