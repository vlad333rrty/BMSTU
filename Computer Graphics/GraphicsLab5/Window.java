import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private static final int[] wb=new int[1];
    private static final int[] hb=new int[1];
    private static long window;
    private static final double fCap=1.0/120;
    static int width,height;
    Window(int w, int h){
        width=w;
        height=h;
        if (!glfwInit()){
            throw new RuntimeException("Error occurred initializing GLFW");
        }
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        window=glfwCreateWindow(w,h,"Lab5",0,0);
        if (window==0){
            throw new RuntimeException("Window was not initialized");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glOrtho(0,w,0,h,-500,500);
    }

    private void update(){
        glfwGetWindowSize(window,wb,hb);
        width=wb[0];
        height=hb[0];

        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0,width,0,height,-500,500);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private double getTime(){
        return (double) System.nanoTime()/1000000000.0;
    }

    void gameLoop(){
        double time,next,ut=0;
        time=getTime();
        glEnable(GL_DEPTH_TEST);
        boolean[] f=new boolean[1];

        DoubleBuffer xb= BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yb=BufferUtils.createDoubleBuffer(1);


        ArrayList<Vector2i> vertices=new ArrayList<>();
        ArrayList<Model> models=new ArrayList<>();

        glfwSetMouseButtonCallback(window,(window,button,action,mods)->{
            if (button==0 && action==0){
                glfwGetCursorPos(window,xb,yb);
                Vector2i v=new Vector2i((int)xb.get(0),-(int)yb.get(0)+height);
                vertices.add(v);
            }
            if (button==1 && action==0){
                if (vertices.size()>1) {
                    Model m=new Model(vertices);
                    if (f[0]) m.isFrame=true;
                    models.add(m);
                    vertices.clear();
                }
            }
        });


        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key==GLFW_KEY_D && action==GLFW_RELEASE && models.size()>0) {
                if (f[0]) {
                    models.clear();
                    f[0]=false;
                }
                else models.remove(models.size() - 1);
            }
            if (key==GLFW_KEY_F && action==0) f[0]=!f[0];
            if (key==GLFW_KEY_C && action==0) {
                ArrayList<Edge> edges=new ArrayList<>();
                //var a=models.get(models.size() - 1).innerCut(models.get(0));
                var a=models.get(models.size() - 1).myCut(models.get(0),edges);
                models.get(0).maintainRighteousness(edges);
                models.remove(models.size()-1);
//                models.remove(0);
//                for (ArrayList<Vector2i> arrayList:a) {
//                    if (arrayList.size()>0) {
//                        Model m = new Model(arrayList);
//                        m.setColour(1, 0, 0);
//                        models.add(m);
//                    }
//                }
//                f[0]=true;
            }
        });

        while (!glfwWindowShouldClose(window)){
            next = getTime();
            ut += next - time;
            time = next;
            for (;ut>=fCap;ut-=fCap){
                update();

                if (vertices.size()>1){
                    glBegin(GL_LINE_STRIP);
                    glColor3d(1,1,1);
                    for (Vector2i t:vertices){
                        glVertex2i(t.x,t.y);
                    }
                    glEnd();
                }

                if (f[0]) for (Model m:models) m.drawVertices();
                else for (Model m:models) m.draw();

                glfwSwapBuffers(window);
                glfwPollEvents();
            }
        }
    }

}
