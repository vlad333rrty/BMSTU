
public class Element<T>
{
    private T val;
    private Element<T> parent;
    private int depth;
    // Создаёт новый элемент со значением x
    public Element(T x)
    {
        val=x;
        parent=this;
    }

    // Возвращает значение элемента
    public T x()
    {
        return val;
    }

    // Определяет, принадлежит ли текущий элемент
    // тому же множеству, что и элемент elem
    public boolean equivalent(Element<T> elem)
    {
        return find(this)==find(elem);
    }

    // Объединяет множество, которому принадлежит текущий
    // элемент, с множеством, которому принадлежит
    // элемент elem
    public void union(Element<T> elem)
    {
        Element<T> rootX=find(this);
        Element<T> rootY=find(elem);
        if (rootX.depth<rootY.depth){
            rootX.parent=rootY;
        }else {
            rootY.parent=rootX;
            if (rootY.depth==rootX.depth && rootX!=rootY) rootX.depth++;
        }
    }

    private Element<T> find(Element<T> element){
        if (element.parent==element) return element;
        element.parent=find(element.parent);
        return element.parent;
    }
}
