import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Window {
    private static final int[] wb=new int[1];
    private static final int[] hb=new int[1];
    private static long window;
    private static final double fCap=1.0/120;
    static int width,height;
    int frameBuffer;
    Window(int w, int h){
        width=w;
        height=h;
        if (!glfwInit()){
            throw new RuntimeException("Error occurred initializing GLFW");
        }
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        window=glfwCreateWindow(w,h,"Lab3",0,0);
        if (window==0){
            throw new RuntimeException("Window was not initialized");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        frameBuffer=glGenFramebuffers();

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

        DoubleBuffer xb= BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yb=BufferUtils.createDoubleBuffer(1);


        ArrayList<Vector2i> vertices=new ArrayList<>();
        ArrayList<Model> models=new ArrayList<>();
        Model[] current=new Model[1];

        int a=200,x=300,y=300;
        vertices.add(new Vector2i(x,y));
        vertices.add(new Vector2i(x+a,y));
        vertices.add(new Vector2i(x+3*a/2,y+a/2*(int)Math.sqrt(3)));
        vertices.add(new Vector2i(x+3*a/2,y+a/2*(int)Math.sqrt(3)+a));
        vertices.add(new Vector2i(x+a,y+a+a*(int)(Math.sqrt(3))));
        vertices.add(new Vector2i(x,y+a+a*(int)(Math.sqrt(3))));
        vertices.add(new Vector2i(x-a/2*(int)Math.sqrt(3),y+a+a/2*(int)(Math.sqrt(3))));
        vertices.add(new Vector2i(x-a/2*(int)Math.sqrt(3),y+a/2*(int)Math.sqrt(3)));
        models.add(new Model(vertices));
        vertices.clear();
        current[0]=models.get(0);

        glfwSetMouseButtonCallback(window,(window,button,action,mods)->{
            if (button==0 && action==0){
                glfwGetCursorPos(window,xb,yb);
                Vector2i v=new Vector2i((int)xb.get(0),-(int)yb.get(0)+height);
                vertices.add(v);
            }
            if (button==1 && action==0){
                if (vertices.size()>1) {
                    models.add(new Model(vertices));
                    vertices.clear();
                    current[0]=models.get(models.size()-1);
                }
            }
        });

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key==GLFW_KEY_F && action==GLFW_RELEASE && current[0]!=null) current[0].changeFlag();
            if (key==GLFW_KEY_S && action==GLFW_RELEASE && current[0]!=null) current[0].changeStyle();
            if (key==GLFW_KEY_D && action==GLFW_RELEASE && models.size()>0) {
                models.remove(models.size() - 1);
                current[0]=models.size()>0 ? models.get(models.size()-1) : null;
            }
            if (key==GLFW_KEY_T && action==GLFW_RELEASE && current[0]!=null) current[0].temp();
            if (key==GLFW_KEY_E && action==GLFW_RELEASE && current[0]!=null) current[0].deleteAfter();
        });

        while (!glfwWindowShouldClose(window)){
            next = getTime();
            ut += next - time;
            time = next;
            for (;ut>=fCap;ut-=fCap){
                update();

                if (vertices.size()>1){
                    glBegin(GL_LINE_STRIP);
                    for (Vector2i t:vertices){
                        glVertex2i(t.x,t.y);
                    }
                    glEnd();
                }

                for (Model m:models) m.draw();

                glfwSwapBuffers(window);
                glfwPollEvents();
            }
        }
        glfwDestroyWindow(window);
    }

}
