import org.joml.Vector2d;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

public class Window {
    private static long window;
    private static Vector4f background;
    private static final double fCap=1.0/60;
    Window(int w,int h){
        if (!GLFW.glfwInit()){
            throw new RuntimeException("Error occurred initializing GLFW");
        }
        window=GLFW.glfwCreateWindow(w,h,"Lab1",0,0);
        if (window==0){
            throw new RuntimeException("Error was not initialized");
        }

        background=new Vector4f(1,1,1,1);
        GLFWMouseButtonCallback event=Input.mouseButtonPressed();

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    private void update(){
        GL11.glClearColor(background.x,background.y,background.z,background.w);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GLFW.glfwPollEvents();
    }

    private double getTime(){
        return (double) System.nanoTime()/1000000000.0;
    }

    void gameLoop(){
        double time,next,ut=0;
        time=getTime();
        Polygon p=new Polygon(new Vector2d[]{
                new Vector2d(-0.5,0.5),
                new Vector2d(0.5,0.5),
                new Vector2d(0.5,-0.5),
                new Vector2d(-0.5,-0.5),
        });
        while (!GLFW.glfwWindowShouldClose(window)){
            next = getTime();
            ut += next - time;
            time = next;
            for (;ut>=fCap;ut-=fCap){
                update();
                p.draw();
                GLFW.glfwSwapBuffers(window);
            }
        }
        GLFW.glfwDestroyWindow(window);
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
