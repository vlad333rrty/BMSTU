import org.joml.Vector3d;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.lwjgl.opengl.GL;

import java.io.FileReader;
import java.io.FileWriter;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private static final int[] wb=new int[1];
    private static final int[] hb=new int[1];
    private static long window;
    private static final double fCap=1.0/60;
    static int width,height;

    private static boolean linear=false,quadratic=false,constant=false;
    private static float[] coef=new float[]{0.001f,0.001f,0.001f,1};

    Window(int w,int h){
        width=w;
        height=h;
        if (!glfwInit()) throw new RuntimeException("Error occurred initializing GLFW");
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        window=glfwCreateWindow(w,h,"Lab6",0,0);
        if (window==0) throw new RuntimeException("Window was not initialized");
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(0);

        glOrtho(0,w,0,h,-10000,10000);
    }

    private void update() {
        glfwGetWindowSize(window, wb, hb);

        width = wb[0];
        height = hb[0];

        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, 0, height, -10000, 10000);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glClearColor(0,0,0, 1);//0.4f, 0.3f, 0.5f, 1
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private double getTime(){
        return (double) System.nanoTime()/1000000000.0;
    }

    private void enable(){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_NORMALIZE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }

    private void disable(){
        glDisable(GL_LIGHT0);
        glDisable(GL_LIGHTING);
    }

    private void initLight(Cylinder m){
        glEnable(GL_LIGHT0);
        glLightfv(GL_LIGHT0,GL_AMBIENT,new float[]{0.25f,0.25f,0.25f,1});
        glLightfv(GL_LIGHT0,GL_DIFFUSE,new float[]{0.5f,0.5f,0.5f,1});
        glLightfv(GL_LIGHT0,GL_SPECULAR,new float[]{0.1f,0.1f,0.1f,1});

        if (constant){
            glLightfv(GL_LIGHT0,GL_CONSTANT_ATTENUATION,coef);
        }else if (linear){
            glLightfv(GL_LIGHT0,GL_LINEAR_ATTENUATION,coef);
        }else if (quadratic){
            glLightfv(GL_LIGHT0,GL_QUADRATIC_ATTENUATION,coef);
        }


        glMaterialfv(GL_FRONT_AND_BACK,GL_AMBIENT,m.amb);
        glMaterialfv(GL_FRONT_AND_BACK,GL_DIFFUSE,m.dif);
        glMaterialfv(GL_FRONT_AND_BACK,GL_SPECULAR,m.spec);
        //glMaterialfv(GL_FRONT_AND_BACK,GL_SHININESS,new float[]{2,2,2,1});
        glLightfv(GL_LIGHT0,GL_POSITION,new float[]{0,0,150,1});
    }

    private void initTextures(){
        Texture simple=new Texture("simple.jpg");
        Texture brick =new Texture("brick.jpg");
        Texture silver =new Texture("silver.jpeg");
        Texture full =new Texture("full.jpeg");
        Texture red =new Texture("red.jpeg");
        Texture gradient =new Texture("Gradient.jpeg");
        Texture four=new Texture("four.jpg");
        four.bind();
    }

    private void init(Cylinder ... models){
        enable();
        for (Cylinder m:models) initLight(m);
    }

    private void save(Cylinder c,Camera camera){
        String path="data.json";
        JSONObject cylinderData=c.toJSON();
        JSONObject cameraData=camera.toJSON();
        JSONObject animationData=c.getAnimation().toJSON();
        JSONObject dataToSave=new JSONObject(cylinderData);
        dataToSave.putAll(cameraData);
        dataToSave.putAll(animationData);
        try (FileWriter file=new FileWriter(path)){
            file.write(dataToSave.toJSONString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void load(Cylinder c,Camera camera){
        String path="data.json";
        JSONParser parser=new JSONParser();
        try{
            JSONObject parseResult=(JSONObject) parser.parse(new FileReader(path));
            JSONObject cylinderData=(JSONObject) parseResult.get("Cylinder");
            JSONObject animationData=(JSONObject) parseResult.get("Animation");
            JSONObject cameraData=(JSONObject) parseResult.get("Camera");
            c.load(cylinderData,animationData);
            camera.load(cameraData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void gameLoop() throws InterruptedException {
        double time,next,ut=0;
        time=getTime();
        int fps=0;
        initTextures();
        Cylinder c=new Cylinder(new Vector3d(),60,80,120,60);
        Camera camera=new Camera(0,0,0);
        init(c);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key==GLFW_KEY_F && action==0) c.changeFlag();
            if (key==GLFW_KEY_ESCAPE && action==0) c.changeAnimationFlag();
            if (key==GLFW_KEY_T && action==0) c.changeTextureFlag();

            if (key==GLFW_KEY_1 && action==0) save(c,camera);
            if (key==GLFW_KEY_2 && action==0) load(c,camera);
            if (key==GLFW_KEY_3 && action==0) {
                constant=true;
                linear=false;
                quadratic=false;
            }
            if (key==GLFW_KEY_4 && action==0) {
                constant=false;
                linear=true;
                quadratic=false;

            }
            if (key==GLFW_KEY_5 && action==0){

                constant=false;
                linear=false;
                quadratic=true;
            }
        });

        while (!glfwWindowShouldClose(window)) {
            next = getTime();
            ut += next - time;
            time = next;

            fps++;
            update();
            input(camera, c);
            initLight(c);
            camera.render();

            c.draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
            if (ut>=1){
                ut=0;
                System.out.println(fps);
                fps=0;
            }

        }
        glfwDestroyWindow(window);
    }

    private void input(Camera camera,Cylinder c){
        if (glfwGetKey(window,GLFW_KEY_W)==1) c.move(0,6,0);
        if (glfwGetKey(window,GLFW_KEY_A)==1) c.move(-6,0,0);
        if (glfwGetKey(window,GLFW_KEY_S)==1) c.move(0,-6,0);
        if (glfwGetKey(window,GLFW_KEY_D)==1) c.move(6,0,0);
        if (glfwGetKey(window,GLFW_KEY_LEFT_CONTROL)==1) c.move(0,0,-6);
        if (glfwGetKey(window,GLFW_KEY_SPACE)==1) c.move(0,0,6);

        if (glfwGetKey(window,GLFW_KEY_X)==1) c.rotate(2,0,0);
        if (glfwGetKey(window,GLFW_KEY_Y)==1) c.rotate(0,2,0);
        if (glfwGetKey(window,GLFW_KEY_Z)==1) c.rotate(0,0,2);
        if (glfwGetKey(window,GLFW_KEY_I)==1) if (c.getStep()<2.09) c.setStep(c.getStep()+0.01);
        if (glfwGetKey(window,GLFW_KEY_O)==1) if (c.getStep()>0.05) c.setStep(c.getStep()-0.01);
        if (glfwGetKey(window,GLFW_KEY_K)==1) if (c.getLineStep()<30) c.setLineStep(c.getLineStep()+1);
        if (glfwGetKey(window,GLFW_KEY_L)==1) if (c.getLineStep()>1) c.setLineStep(c.getLineStep()-1);

        if (glfwGetKey(window,GLFW_KEY_EQUAL)==1) c.centre.add(0,0,5);
        if (glfwGetKey(window,GLFW_KEY_B)==1){
            coef[0]+=0.01f;
            initLight(c);
        }
        if (glfwGetKey(window,GLFW_KEY_N)==1){
            coef[1]+=0.01f;
            initLight(c);
        }
        if (glfwGetKey(window,GLFW_KEY_M)==1){
            coef[2]+=0.01f;
            initLight(c);
        }
        if (glfwGetKey(window,GLFW_KEY_6)==1){
            coef[0]-=0.01f;
            initLight(c);
        }
        if (glfwGetKey(window,GLFW_KEY_7)==1){
            coef[1]-=0.01f;
            initLight(c);
        }
        if (glfwGetKey(window,GLFW_KEY_8)==1){
            coef[2]-=0.01f;
            initLight(c);
        }
    }

}