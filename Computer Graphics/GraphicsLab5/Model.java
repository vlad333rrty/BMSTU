import org.joml.Vector2i;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class Model {
    private final ArrayList<Edge> edges =new ArrayList<>();
    private final ArrayList<Vector2i> vertices =new ArrayList<>();
    private double r=1,g=1,b=1;
    boolean isFrame;
    Model(ArrayList<Vector2i> vectors){
        for (int i=0;i<vectors.size()-1;i++){
            vertices.add(new Vector2i(vectors.get(i)));
            addEdge(vectors.get(i),vectors.get(i+1));
        }
        addEdge(vectors.get(vectors.size()-1),vectors.get(0));
        vertices.add(new Vector2i(vectors.get(vectors.size()-1)));
    }

    private void addEdge(Vector2i u, Vector2i v){
        edges.add(new Edge(u,v));
    }

    void setColour(double r,double g,double b){
        this.r=r;
        this.g=g;
        this.b=b;
    }

    void draw(){
        for (Edge e:edges)
            e.draw(r,g,b);
    }

    void drawVertices(){
        glBegin(GL_POINTS);

        double c=1,k=0;
        for (Vector2i v:vertices){
            glColor3d(c,0,k);
            glVertex2i(v.x,v.y);
            glVertex2i(v.x+1,v.y);
            glVertex2i(v.x-1,v.y);
            glVertex2i(v.x,v.y+1);
            glVertex2i(v.x,v.y-1);
            c-=0.15;
            k+=0.15;
        }
        glEnd();
    }

//    ArrayList<ArrayList<Vector2i>> cut(Model m){
//        ArrayList<Vector2i> result=new ArrayList<>(m.vertices);
//        ArrayList<ArrayList<Vector2i>> r=new ArrayList<>();
//        ArrayList<Vector2i> intersections=new ArrayList<>();
//        Vector2i cp1=vertices.get(vertices.size()-1),cp2,s,e;
//        for (Vector2i value : vertices) {
//            cp2 = value;
//            result.clear();
//            s = m.vertices.get(m.vertices.size() - 1);
//            for (Vector2i vertex : m.vertices) {
//                e = vertex;
//                if (!isInside(e,cp1,cp2)){
//                    if (isInside(s,cp1,cp2)) {
//                        Vector2i intersection=getIntersection(e, s, cp1, cp2);
//                        intersections.add(intersection);
//                        result.add(intersection);
//                    }
//                    result.add(e);
//                }else if (!isInside(s,cp1,cp2)){
//                    Vector2i intersection=getIntersection(e, s, cp1, cp2);
//                    intersections.add(intersection);
//                    result.add(intersection);
//                }
//                s = e;
//            }
//
//            r.add(new ArrayList<>(result));
//            cp1 = cp2;
//        }
//        return r;
//    }
//
//    ArrayList<ArrayList<Vector2i>> innerCut(Model m){
//        ArrayList<Vector2i> result=new ArrayList<>(m.vertices);
//        ArrayList<ArrayList<Vector2i>> r=new ArrayList<>();
//        ArrayList<Vector2i> input=new ArrayList<>();
//        ArrayList<Vector2i> intersections=new ArrayList<>();
//        Vector2i cp1=vertices.get(vertices.size()-1),cp2,s,e;
//        for (Vector2i value : vertices) {
//            cp2 = value;
//            input.clear();
//            input.addAll(result);
//            result.clear();
//            s = input.get(input.size() - 1);
//            for (Vector2i vertex : input) {
//                e = vertex;
//                if (isInside(e, cp1, cp2)) {
//                    if (!isInside(s, cp1, cp2)) {
//                        Vector2i i=getIntersection(e, s, cp1, cp2);
//                        result.add(i);
//                        intersections.add(i);
//                    }
//                    result.add(e);
//                } else if (isInside(s, cp1, cp2)) {
//                    Vector2i i=getIntersection(e, s, cp1, cp2);
//                    result.add(i);
//                    intersections.add(i);
//                }
//                s = e;
//            }
//            cp1 = cp2;
//        }
//        r.add(new ArrayList<>(result));
//        return r;
//    }

    ArrayList<ArrayList<Vector2i>> myCut(Model m,ArrayList<Edge> edges){
        ArrayList<ArrayList<Vector2i>> result=new ArrayList<>();
        ArrayList<Vector2i> input=new ArrayList<>();
        ArrayList<Vector2i> output=new ArrayList<>(m.vertices);
        Vector2i s,e,scp=vertices.get(vertices.size()-1),ecp;
        for (Vector2i v:vertices){
            ecp=v;
            input.addAll(output);
            output.clear();
            s=input.get(input.size()-1);
            for (Vector2i pv:input){
                e=pv;
                if (isInside(e,scp,ecp) && isInside(s,scp,ecp)){
                    output.add(e);
                }else if (isInside(s,scp,ecp) && !isInside(e,scp,ecp)){
                    output.add(getIntersection(s,e,scp,ecp));
                }else if (!isInside(s,scp,ecp) && isInside(e,scp,ecp)){
                    output.add(getIntersection(s,e,scp,ecp));
                    output.add(e);
                }
                s=e;
            }
            scp=ecp;
            input.clear();
        }
        //result.add(check(output));
        result.add(output);
        //nextTry(output);
        edges.addAll(whatYouAreDoingIsWrong(output));
        return result;
    }

//
//    ArrayList<Edge> nextTry(ArrayList<Vector2i> vertices){
//        ArrayList<Edge> edges=new ArrayList<>();
//        int i;
//        for (i=0;i<vertices.size();i++) edges.add(new Edge(vertices.get(i),vertices.get((i+1)%vertices.size())));
//        l:for (i=0;i<edges.size();i++){
//            for (int j=i+1;j<edges.size();j++){
//                if (edges.get(j).isCollinear(edges.get(i))) break l;
//            }
//        }
//        System.out.println(i);
//        if (i== edges.size()) return null;
//        ArrayList<Edge> collinearEdges=new ArrayList<>();
//        Edge minC=edges.get(i);
//        for (Edge edge : edges) {
//            if (edge.isCollinear(minC)) {
//                if (edge.lengthSquared() < minC.lengthSquared()) minC = edge;
//                collinearEdges.add(edge);
//            }
//        }
//        collinearEdges.remove(minC);
//        for (int j=0;j<collinearEdges.size();j++){
//            Edge e1,e2;
//            if (collinearEdges.get(j).a.sub(minC.a,new Vector2i()).lengthSquared()
//                    <
//                    collinearEdges.get(j).a.sub(minC.b,new Vector2i()).lengthSquared()){
//                e1=new Edge(new Vector2i(collinearEdges.get(j).a),new Vector2i(minC.a));
//                e2=new Edge(new Vector2i(collinearEdges.get(j).b),new Vector2i(minC.b));
//            }else{
//                e1=new Edge(new Vector2i(collinearEdges.get(j).a),new Vector2i(minC.b));
//                e2=new Edge(new Vector2i(collinearEdges.get(j).b),new Vector2i(minC.a));
//            }
//            edges.remove(minC);
//            edges.remove(collinearEdges.get(j));
//            edges.add(e1);
//            edges.add(e2);
//            break; // delete after
//        }
//
//        System.out.println(edges);
//
//        this.edges.clear();
//        this.edges.addAll(edges);
//
//        return edges;
//    }

    ArrayList<Edge> whatYouAreDoingIsWrong(ArrayList<Vector2i> vertices){
        ArrayList<Edge> edges=new ArrayList<>();
        int i;
        for (i=0;i<vertices.size();i++) edges.add(new Edge(vertices.get(i),vertices.get((i+1)%vertices.size())));
        while (finalTry(edges));
        return edges;
    }


    void maintainRighteousness(ArrayList<Edge> edges){
        this.edges.addAll(edges);
        while (finalTry(this.edges));
//        r=1;
//        g=b=0;
    }

    private boolean finalTry(ArrayList<Edge> edges){
        int i,j=0;
        l:for (i=0;i<edges.size();i++){
            for (j=i+1;j<edges.size();j++){
                if (edges.get(j).isCollinear(edges.get(i))
                        &&
                        (projectionIntersection(edges.get(j).a.x,edges.get(j).b.x,edges.get(i).a.x,edges.get(i).b.x)
                                &&
                                projectionIntersection(edges.get(j).a.y,edges.get(j).b.y,edges.get(i).a.y,edges.get(i).b.y)))
                    break l;
            }
        }

        if (i==edges.size()) return false;

        Edge minC,maxC,e1,e2;
        if (edges.get(i).lengthSquared()<edges.get(j).lengthSquared()){
            minC=edges.get(i);
            maxC=edges.get(j);
        }else{
            minC=edges.get(j);
            maxC=edges.get(i);
        }

        if (maxC.a.sub(minC.a,new Vector2i()).lengthSquared()<maxC.a.sub(minC.b,new Vector2i()).lengthSquared()){
            e1=new Edge(new Vector2i(maxC.a),new Vector2i(minC.a));
            e2=new Edge(new Vector2i(maxC.b),new Vector2i(minC.b));
        }else {
            e1=new Edge(new Vector2i(maxC.a),new Vector2i(minC.b));
            e2=new Edge(new Vector2i(maxC.b),new Vector2i(minC.a));
        }

        edges.remove(j);
        edges.remove(i);
        edges.add(e1);
        edges.add(e2);

        return true;
    }

    private boolean fits(Vector2i point,Vector2i a,Vector2i b){
        return Math.min(a.x,b.x)<=point.x && point.x<=Math.max(a.x,b.x)
                && Math.min(a.y,b.y)<=point.y && point.y<=Math.min(a.y,b.y);
    }

    private Vector2i getIntersection(Vector2i a, Vector2i b, Vector2i c, Vector2i d){
        int p=b.x-a.x,q=b.y-a.y;
        int l=d.x-c.x,m=d.y-c.y;
        int delta=det2x2(p,l,q,m);
        float t=det2x2(c.x-a.x,l,c.y-a.y,m)/(float)delta;
        return new Vector2i(Math.round(a.x+p*t),Math.round(a.y+q*t));
    }

    private int det2x2(int a,int b,int c,int d){
        return a*d-b*c;
    }

    private int det2x2(Vector2i u,Vector2i v){
        return u.x*v.y-u.y*v.x;
    }

    private boolean isInside(Vector2i point, Vector2i a, Vector2i b){
        return det2x2(b.x-a.x,b.y-a.y,point.x-a.x,point.y-a.y)>0;
    }
    private boolean intersects(Vector2i a, Vector2i b, Vector2i c, Vector2i d){
        boolean xIntersection=projectionIntersection(a.x,b.x,c.x,d.y);
        boolean yIntersection=projectionIntersection(a.y,b.y,c.y,d.y);
        return  xIntersection && yIntersection;
    }

    private boolean projectionIntersection(int a,int b,int c,int d){
        boolean res;
        int max,min,max1,min1;
        if (a>b) {
            max = a;
            min=b;
        }
        else {
            max = b;
            min=a;
        }
        if (c>d) {
            max1 = c;
            min1=d;
        }
        else {
            max1 = d;
            min1=c;
        }
        if (max>max1){
            res=min-max1<0;
        }else{
            res=min1-max<0;
        }
        return res;
    }

}

class Edge {
    Vector2i a,b;
    int k,c,maxX,maxY,minX,minY;
    boolean isVertical,isHorizontal;
    Edge(Vector2i a, Vector2i b) {
        this.a = a;
        this.b = b;
        if (a.x == b.x) isVertical = true;
        else {
            isHorizontal=a.y-b.y==0;
            k = (a.y - b.y) / (a.x - b.x);
            c = b.y - k * b.x;
        }
        maxX=Math.max(a.x,b.x);
        minX=Math.min(a.x,b.x);
        minY=Math.min(a.y,b.y);
        maxY=Math.max(a.y,b.y);
    }

    double getY(int x){
        if (isVertical) return a.y;
        return k*x+c;
    }

    void draw(double r,double g,double b){
        glBegin(GL_LINES);
        glColor3d(r,g,b);
        glVertex2i(this.a.x,this.a.y);
        glVertex2i(this.b.x,this.b.y);
        glEnd();
    }

    int lengthSquared(){
        return (b.x-a.x)*(b.x-a.x)+(b.y-a.y)*(b.y-a.y);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    boolean isCollinear(Edge e){
        int p=b.x-a.x,q=b.y-a.y;
        int l=e.b.x-e.a.x,m=e.b.y-e.a.y;

        return Math.abs(p*m-q*l)<=400;
    }
}