import org.joml.Vector2i;
import java.util.ArrayList;
import java.util.Collections;

import static org.lwjgl.opengl.GL11.*;

public class Model {
    private ArrayList<Edge> dots=new ArrayList<>();
    private ArrayList<Edge> edges =new ArrayList<>();
    private ArrayList<Edge> cap=new ArrayList<>();
    private int max,min,left,right;
    private boolean flag,t;
    Model(ArrayList<Vector2i> vectors){
        for (int i=0;i<vectors.size()-1;i++){
            addEdge(vectors.get(i),vectors.get(i+1));
            dots.add(new Edge(vectors.get(i),vectors.get(i+1)));
        }
        addEdge(vectors.get(vectors.size()-1),vectors.get(0));
        dots.add(new Edge(vectors.get(vectors.size()-1),vectors.get(0)));
        Collections.sort(edges);
        min=edges.get(edges.size()-1).endPoint.y;
        for (Edge e:edges) if (e.endPoint.y<min) min=e.endPoint.y;
        max=edges.get(0).startPoint.y;
        int m=10000,M=0;
        for (Edge e:dots){
            if (M<Math.max(e.startPoint.x,e.endPoint.x)) M=Math.max(e.startPoint.x,e.endPoint.x);
            if (m>Math.min(e.startPoint.x,e.endPoint.x)) m=Math.min(e.startPoint.x,e.endPoint.x);
        }
        left=m;
        right=M;
        for (Edge e:dots) e.setOwner(this);
    }

    void changeStyle(){
        for (Edge e:dots) e.changeStyle();
    }

    private void addEdge(Vector2i u,Vector2i v){
        if (u.y-v.y!=0) edges.add(new Edge(u,v));
    }

    void draw(){
        drawFrame();
        if (flag) fillR();
    }

    private void drawFrame(){
          for (Edge e:dots) e.bre();
    }

    void changeFlag(){flag=!flag;}

    void fillR() {
        ArrayList<Integer> intersections = new ArrayList<>();
        int height=max-min+1,width=right-left+1;
        float[] data=new float[height*width*3];

        for (int y = max,x; y >= min; y--) {
            for (int i=0;i<cap.size();){
                if (cap.get(i).endPoint.y>=y) cap.remove(cap.get(i));
                else i++;
            }
            for (Edge e:edges){
                if (e.startPoint.y==y) cap.add(e);
            }
            for (Edge e:cap){
                intersections.add((int)e.getX(y));
            }
            intersections.sort(Integer::compareTo);
            for (int i=0,ind;i<intersections.size()-1;i+=2){
                for (x=intersections.get(i)+1;x<=intersections.get(i+1);x++){
                    ind=(width*(y-min)+(x-left))*3;
                    data[ind]=0;
                    data[ind+1]=1;
                    data[ind+2]=0;
                }
            }
            intersections.clear();
        }
        cap.clear();
        glRasterPos2i(left,min);
        glDrawPixels(width,height,GL_RGB,GL_FLOAT,data);
    }

    boolean isInside(Vector2i point){
        int c=0;
        double intersection;
        for (Edge e:dots){
            intersection=e.getX(point.y);
            if (Math.min(e.startPoint.x,e.endPoint.x)<=intersection
                    && intersection<=Math.max(e.startPoint.x,e.endPoint.x)
                    && intersection>point.x) c++;
        }
        return c%2==1;
    }

    void temp(){
        for (Edge e:dots) e.temp();
    }
    void deleteAfter(){
        t=!t;
    }

}


class Edge implements Comparable<Edge>{
    Vector2i startPoint,endPoint,u,v;
    double k,b;
    private boolean vertical;
    private  boolean standard=true,left;
    private Model owner;
    Edge(Vector2i a,Vector2i b){
        left=b.y-a.y>0;
        u=a;
        v=b;
        if (a.y>b.y){
            startPoint=a;
            endPoint=b;
        }else{
            startPoint=b;
            endPoint=a;
        }
        if (startPoint.x==endPoint.x) vertical=true;
        if (startPoint.y!=endPoint.y){
            k = (startPoint.x - endPoint.x) / (double) (startPoint.y - endPoint.y);
            this.b = endPoint.x-k*endPoint.y;
        }
    }
    @Override
    public int compareTo(Edge o) {
        return -Integer.compare(startPoint.y,o.startPoint.y);
    }

    @Override
    public String toString() {
        return  startPoint +" , "+endPoint;
    }

    void setOwner(Model model){
        owner=model;
    }

    double getX(int y){
        if (vertical) return startPoint.x;
        return k*y+b;
    }

    void changeStyle(){
        standard=!standard;
    }

    void temp(){left=!left;}

    void bre(){
        glBegin(GL_POINTS);
        int x1=u.x,x2=v.x,y1=u.y,y2=v.y;
        int dx=x2-x1,dy=y2-y1;

        if (Math.abs(dy)>Math.abs(dx)){
            if (left){
                if (x1>x2){
                    x1=v.x;
                    x2=u.x;
                    y1=v.y;
                    y2=u.y;
                }
            }else{
                if (x1<x2){
                    x1=v.x;
                    x2=u.x;
                    y1=v.y;
                    y2=u.y;
                }
            }
        }else{
            if (left){
                if (x1<x2){
                    x1=v.x;
                    x2=u.x;
                    y1=v.y;
                    y2=u.y;
                }
            }else {
                if (x1>x2){
                    x1=v.x;
                    x2=u.x;
                    y1=v.y;
                    y2=u.y;
                }
            }
        }
        dx=x2-x1;
        dy=y2-y1;
        int sx=dx>0 ? 1:-1,sy=dy>0 ? 1:-1;
        dx*=sx;
        dy*=sy;
        int dMax,dMin;
        boolean flag;
        if (flag=dx>dy){
            dMax=dx;
            dMin=dy;
        }else{
            dMax=dy;
            dMin=dx;
        }

        int e=0;
        double i=1/2.;
        while (x1!=x2 || y1!=y2){
            glColor3d(0,i,0);
            glVertex2i(x1,y1);
            e+=dMin;
            if (2*e>dMax){
                if (flag) y1+=sy;
                else x1+=sx;
                e-=dMax;
            }
            if (flag) x1+=sx;
            else y1+=sy;
            i=Math.abs(e/(double)dMax+0.5);
        }
        glEnd();
    }
}