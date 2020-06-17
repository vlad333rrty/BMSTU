import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import  org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

public class Window {
    private static int[] wb=new int[1],hb=new int[1];
    private static long window;
    private static Vector4f background;
    private static final double fCap=1.0/60;
    static int width,height;
    private static Model current;
    private static ArrayList<Model> models=new ArrayList<>();
    Window(int w,int h){
        width=w;
        height=h;
        if (!GLFW.glfwInit()){
            throw new RuntimeException("Error occurred initializing GLFW");
        }
        GLFW.glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        window=GLFW.glfwCreateWindow(w,h,"Lab2",0,0);
        if (window==0){
            throw new RuntimeException("Window was not initialized");
        }

        background=new Vector4f(0.4f,0.3f,0.5f,1); //0.4f,0.3f,0.5f,1
        GLFWMouseButtonCallback event=Input.mouseButtonPressed();

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glOrtho(0,w,0,h,-500,500);
    }

    private void update(){
        GLFW.glfwGetWindowSize(window,wb,hb);
        width=wb[0];
        height=hb[0];

        GL11.glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0,width,0,height,-500,500);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        GL11.glClearColor(background.x,background.y,background.z,background.w);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GLFW.glfwPollEvents();
    }

    private double getTime(){
        return (double) System.nanoTime()/1000000000.0;
    }

    void gameLoop(){
        double time,next,ut=0;
        time=getTime();
        Cylinder c=new Cylinder(new Vector3f(600f,600f,0),100,80,120,40);
        Cube rightCube=new Cube(50,new Vector3f(600,200,0));
        models.add(c);
        models.add(rightCube);
        current=c;
        glEnable(GL_DEPTH_TEST);
        double t=PI*35.26/180,phi=PI/4;
        double[] m=new double[]{
                cos(phi), sin(phi)*sin(t), sin(phi)*cos(t),0,0,cos(t),-sin(t),0,sin(phi), -cos(phi)*sin(t), -cos(phi)*cos(t),
                0,0,0,0,1,
        };
        while (!GLFW.glfwWindowShouldClose(window)){
            next = getTime();
            ut += next - time;
            time = next;
            for (;ut>=fCap;ut-=fCap){
                glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                    if (key==GLFW_KEY_F && action==GLFW_RELEASE) current.changeFlag();
                });
                update();
                handleInput();

                glEnable(GL_CULL_FACE);
                rightCube.draw(m);
                glDisable(GL_CULL_FACE);

                c.draw(m);

                GLFW.glfwSwapBuffers(window);
            }
        }
        GLFW.glfwDestroyWindow(window);
    }

    private static void handleInput(){
        if (Input.isPressed(GLFW_KEY_W)){
            current.centre.add(0,2,0);
        }
        if (Input.isPressed(GLFW_KEY_A)){
            current.centre.add(-2,0,0);
        }
        if (Input.isPressed(GLFW_KEY_S)){
            current.centre.add(0,-2,0);
        }
        if (Input.isPressed(GLFW_KEY_D)){
            current.centre.add(2,0,0);
        }
        if (Input.isPressed(GLFW_KEY_SPACE)){
            current.centre.add(0,0,2);
        }
        if (Input.isPressed(GLFW_KEY_LEFT_CONTROL)){
            current.centre.add(0,0,-2);
        }
        if (Input.isPressed(GLFW_KEY_1)){
            current=models.get(0);
        }
        if (Input.isPressed(GLFW_KEY_2)){
            current=models.get(1);
        }
        if (Input.isPressed(GLFW_KEY_3)){
            current=models.get(2);
        }
        if (Input.isPressed(GLFW_KEY_4)){
            current=models.get(3);
        }
        if (Input.isPressed(GLFW_KEY_X)){
            current.angleX++;
        }
        if (Input.isPressed(GLFW_KEY_Y)){
            current.angleY++;
        }
        if (Input.isPressed(GLFW_KEY_Z)){
            current.angleZ++;
        }
        if (Input.isPressed(GLFW_KEY_I) && current instanceof Cylinder){
            var c=(Cylinder)current;
            if (c.getStep()<60) c.setStep(c.getStep()+0.01);
        }
        if (Input.isPressed(GLFW_KEY_O) && current instanceof Cylinder){
            var c=(Cylinder)current;
            if (c.getStep()>0.1) c.setStep(c.getStep()-0.01);
        }
        if (Input.isPressed(GLFW_KEY_L) && current instanceof Cylinder){
            var c=(Cylinder)current;
            if (c.getLineStep()<200) c.setLineStep(c.getLineStep()+1);
        }
        if (Input.isPressed(GLFW_KEY_K) && current instanceof Cylinder){
            var c=(Cylinder)current;
            if (c.getLineStep()>10) c.setLineStep(c.getLineStep()-1);
        }
    }

    static class Input extends GLFWCursorPosCallback {
        static boolean mouseRightButton,mouseLeftButton;
        static double posX,posY;
        @Override
        public void invoke(long window, double xpos, double ypos) {
            posX=xpos;
            posY=ypos;
        }
        static boolean isPressed(int key){
            return glfwGetKey(window,key)==1;
        }
        static GLFWMouseButtonCallback mouseButtonPressed(){
            GLFWMouseButtonCallback event;
            glfwSetMouseButtonCallback(window,event=GLFWMouseButtonCallback.create((window,button,action,mods)->{
                if (button==0){
                    if (action==1) mouseLeftButton=true;
                    if (action==0) mouseLeftButton=false;

                }else if (button==1){
                    if (action==1) mouseRightButton=true;
                    if (action==0) mouseRightButton=false;
                }
            }));
            return event;
        }
    }
}

