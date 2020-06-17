import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;

abstract class  Model {
    void draw() {}
    int angleX = 0,angleY = 0,angleZ = 0;
    Vector3f centre=new Vector3f();
    void changeFlag(){};
}

class Cube extends Model{
    private int a;

    Cube(int a) {
        this.a = a;
    }
    Cube(int a,Vector3f centre){
        this.a=a;
        this.centre=centre;
    }

    void draw() {
        glPushMatrix();
        glTranslatef(centre.x,centre.y,centre.z);
        glRotatef(angleX,1,0,0);
        glRotatef(angleY,0,1,0);
        glRotatef(angleZ,0,0,1);

        glBegin(GL_QUADS);
        glColor3f(0.2f, 0.8f, 0.2f);
        glVertex3f(a, a, -a);
        glVertex3f(-a, a, -a);
        glVertex3f(-a, a, a);
        glVertex3f(a, a, a);

        glColor3f(0f, 0.25f, 0.5f);
        glVertex3f(a, -a, a);
        glVertex3f(-a, -a, a);
        glVertex3f(-a, -a, -a);
        glVertex3f(a, -a, -a);

        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(a, a, a);
        glVertex3f(-a, a, a);
        glVertex3f(-a, -a, a);
        glVertex3f(a, -a, a);

        glColor3f(1.0f, 1f, 0.0f);
        glVertex3f(a, -a, -a);
        glVertex3f(-a, -a, -a);
        glVertex3f(-a, a, -a);
        glVertex3f(a, a, -a);

        glColor3f(0, 0.2f, 1);
        glVertex3f(-a, a, a);
        glVertex3f(-a, a, -a);
        glVertex3f(-a, -a, -a);
        glVertex3f(-a, -a, a);

        glColor3f(1.0f, 0.4f, 1.0f);
        glVertex3f(a, a, -a);
        glVertex3f(a, a, a);
        glVertex3f(a, -a, a);
        glVertex3f(a, -a, -a);
        glEnd();

        glPopMatrix();
    }

    void draw(double[] m) {
        glPushMatrix();
        glTranslatef(centre.x,centre.y,centre.z);
        glRotatef(angleX,1,0,0);
        glRotatef(angleY,0,1,0);
        glRotatef(angleZ,0,0,1);
        glMultMatrixd(m);

        glBegin(GL_QUADS);
        glColor3f(0.2f, 0.8f, 0.2f);
        glVertex3f(a, a, -a);
        glVertex3f(-a, a, -a);
        glVertex3f(-a, a, a);
        glVertex3f(a, a, a);

        glColor3f(0f, 0.25f, 0.5f);
        glVertex3f(a, -a, a);
        glVertex3f(-a, -a, a);
        glVertex3f(-a, -a, -a);
        glVertex3f(a, -a, -a);

        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(a, a, a);
        glVertex3f(-a, a, a);
        glVertex3f(-a, -a, a);
        glVertex3f(a, -a, a);

        glColor3f(1.0f, 1f, 0.0f);
        glVertex3f(a, -a, -a);
        glVertex3f(-a, -a, -a);
        glVertex3f(-a, a, -a);
        glVertex3f(a, a, -a);

        glColor3f(0, 0.2f, 1);
        glVertex3f(-a, a, a);
        glVertex3f(-a, a, -a);
        glVertex3f(-a, -a, -a);
        glVertex3f(-a, -a, a);

        glColor3f(1.0f, 0.4f, 1.0f);
        glVertex3f(a, a, -a);
        glVertex3f(a, a, a);
        glVertex3f(a, -a, a);
        glVertex3f(a, -a, -a);
        glEnd();

        glPopMatrix();
    }
}
class Cylinder extends Model{
    protected double a,b,height,step=0.1,dist,lineStep=20;
    protected boolean flag=false;
    ArrayList<Vector3d> side =new ArrayList<>();
    ArrayList<Vector3d> basement =new ArrayList<>();
    ArrayList<Vector3d> line=new ArrayList<>();

    Cylinder(Vector3f centre, double a,double b, double height) {
        this.centre = centre;
        this.a = a;
        this.b = b;
        this.height = height;
        calc();
    }

    Cylinder(Vector3f centre, double a,double b, double height,double dist) {
        this.centre = centre;
        this.a = a;
        this.b = b;
        this.height = height;
        this.dist = dist;
        calc();
    }

    public void setLineStep(double lineStep) {
        this.lineStep = lineStep;
        reCalc();
    }

    public double getLineStep() {
        return lineStep;
    }

    protected  void calc(){
        double height=this.height/2;
        double x,y,angle,pi2=2*PI;
        for (angle=0;angle<pi2;angle+=step){
            x = a * cos(angle);
            y = b * sin(angle);
            side.add(new Vector3d(x-dist,y,height));
            side.add(new Vector3d(x, y, -height));
        }
        side.add(new Vector3d(a-dist, 0, height));
        side.add(new Vector3d(a, 0, -height));

        for (var i=-height+lineStep;i<height;i+=lineStep){
            for (angle=0;angle<pi2;angle+=step){
                x = a * cos(angle);
                y = b * sin(angle);
                line.add(new Vector3d(-dist/2/height*(i+height)+x,y,i));
            }
            line.add(new Vector3d(-dist/2/height*(i+height)+a,0,i));
        }

        for (angle=0;angle<pi2;angle+=step){
            x = a * cos(angle);
            y = b * sin(angle);
            basement.add(new Vector3d(x, y , height));
        }
        basement.add(new Vector3d(a, 0, height));
    }

    protected void reCalc(){
        basement.clear();
        side.clear();
        line.clear();
        calc();
    }

    double getStep() {
        return step;
    }

    public Vector3f getCentre() {
        return centre;
    }

    void setStep(double step) {
        this.step = step;
        reCalc();
    }

    void changeFlag(){
        flag=!flag;
    }

    void draw(){
        glPushMatrix();
        glTranslatef(centre.x,centre.y,centre.z);
        glRotatef(angleX,1,0,0);
        glRotatef(angleY,0,1,0);
        glRotatef(angleZ,0,0,1);

        if (flag){
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
            drawSide();
            drawBasementCircle();
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
        }else {
            drawSide();
            drawBasements();
        }

        glPopMatrix();
    }

    void draw(double[] m){
        glPushMatrix();
        glTranslatef(centre.x,centre.y,centre.z);
        glRotatef(angleX,1,0,0);
        glRotatef(angleY,0,1,0);
        glRotatef(angleZ,0,0,1);
        glMatrixMode(GL_PROJECTION);
        glMultMatrixd(m);
        glMatrixMode(GL_MODELVIEW);

        if (flag){
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
            drawSide();
            drawBasementCircle();
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
        }else {
            drawSide();
            drawBasements();
        }

        glPopMatrix();
    }

    protected void drawSide(){
        glBegin(GL_QUAD_STRIP);
        glColor3d(0,0.8,0);
        for (Vector3d v: side){
            glVertex3d(v.x,v.y,v.z);
        }
        glEnd();

        glBegin(GL_LINE_STRIP);
        for (Vector3d v:line){
            glVertex3d(v.x,v.y,v.z);
        }
        glEnd();
    }

    protected void drawBasements(){
        glBegin(GL_POLYGON);
        glColor3d(0,0,1);
        for (Vector3d v: basement){
            glVertex3d(v.x-dist,v.y,v.z);
        }
        glEnd();

        glBegin(GL_POLYGON);
        for (Vector3d v: basement){
            glVertex3d(v.x,v.y,-v.z);
        }
        glVertex3d(a, 0, -height);
        glEnd();
    }

    protected void drawBasementCircle(){
        glColor3d(0,0.8,0);
        for (double i=0,c=0.25;i<3;i++,c+=0.25){
            glBegin(GL_LINE_STRIP);
            for (Vector3d v: basement){
                glVertex3d(v.x*c-dist,v.y*c,v.z);
            }
            glEnd();
            glBegin(GL_LINE_STRIP);
            for (Vector3d v: basement){
                glVertex3d(v.x*c,v.y*c,-v.z);
            }

            glEnd();
        }

        glBegin(GL_LINES);
        for (int i = 0; i< side.size(); i+=2){
            glVertex3d(-dist,0,height/2);
            glVertex3d(side.get(i).x, side.get(i).y, side.get(i).z);
        }
        for (int i = 1; i< side.size(); i+=2){
            glVertex3d(0,0,-height/2);
            glVertex3d(side.get(i).x, side.get(i).y, side.get(i).z);
        }
        glEnd();
    }
}
