import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.lwjgl.opengl.GL;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private static final int[] wb=new int[1];
    private static final int[] hb=new int[1];
    private static long window;
    static int width,height;

    Window(int w,int h){
        width=w;
        height=h;
        if (!glfwInit()) throw new RuntimeException("Error occurred initializing GLFW");
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);

        window=glfwCreateWindow(w,h,"Lab8",0,0);
        if (window==0) throw new RuntimeException("Window was not initialized");
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glOrtho(0,w,0,h,-10000,10000);
    }

    @SuppressWarnings("unused")
    private void resize(){
        glfwGetWindowSize(window, wb, hb);

        width = wb[0];
        height = hb[0];
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, 0, height, -10000, 10000);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

    }

    private void update() {
        glClearColor(0,0,0, 1);//0.4f, 0.3f, 0.5f, 1
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }


    @SuppressWarnings("unused")
    private void initTextures(){
        Texture simple=new Texture("simple.jpg");       //0
        Texture brick =new Texture("brick.jpg");        //1
        Texture silver =new Texture("silver.jpeg");     //2
        Texture full =new Texture("full.jpeg");         //3
        Texture red =new Texture("red.jpeg");           //4
        Texture gradient =new Texture("Gradient.jpeg"); //5
        Texture four=new Texture("four.jpg");           //6
    }

    void gameLoop() {
        glEnable(GL_DEPTH_TEST);
        initTextures();

        Cylinder c=new Cylinder(new Vector3f(),80,70,100,20);
        Shader shader=new Shader("vertexShader.glsl","fragmentShader.glsl");
        c.setShader(shader);

        Matrix4f model=new Matrix4f().identity();
        Matrix4f view=new Matrix4f().identity();
        Matrix4f projection=new Matrix4f().identity();
        Matrix4f dest=new Matrix4f().identity();
        projection.ortho(0,width,0,height,-10000,10000);

        int[] z=new int[]{0,0,150};
        float[] attenuation=new float[]{1,0.001f,0.0001f};

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key==GLFW_KEY_F && action==0) c.changeFlag();
            if (key==GLFW_KEY_1 && action==0) c.changeAnimationFlag();
            if (key==GLFW_KEY_T && action==0) c.changeTextureFlag();
            if (key==GLFW_KEY_2 && action==0) save(c,model,view,projection);
            if (key==GLFW_KEY_3 && action==0) load(c,model,view,projection);

            if (key==GLFW_KEY_6 && action==0) if (attenuation[0]>0.1f) attenuation[0]-=0.1f;
            if (key==GLFW_KEY_7 && action==0) if (attenuation[0]<2) attenuation[0]+=0.1f;

            if (key==GLFW_KEY_V && action==0) if (attenuation[1]>0.001f) attenuation[0]-=0.001f;
            if (key==GLFW_KEY_B && action==0) if (attenuation[2]>0.0001f) attenuation[1]-=0.0001f;
            if (key==GLFW_KEY_N && action==0) if (attenuation[1]<1f) attenuation[0]+=0.001f;
            if (key==GLFW_KEY_M && action==0) if (attenuation[2]<0.1f) attenuation[1]+=0.0001f;
        });


        shader.bind();
        setUniforms(shader);
        while (!glfwWindowShouldClose(window)) {
            update();

            input(c,model,view,z);

            shader.setUniform("m",model);
            shader.setUniform("mvp",projection.mul(view,dest).mul(model));
            shader.setUniform("light.position",z[0],z[1],z[2]);
            shader.setUniform("light.attenuation",attenuation[0],attenuation[1],attenuation[2]);

            c.draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        glfwDestroyWindow(window);
    }

    @SuppressWarnings("unchecked")
    private void save(Cylinder c,Matrix4f model,Matrix4f view,Matrix4f projection){
        JSONObject cylinderData=c.toJSON();
        JSONObject animationData=c.getAnimation().toJSON();
        JSONObject dataToSave=new JSONObject(cylinderData);
        dataToSave.putAll(animationData);

        JSONObject matrices=new JSONObject();
        JSONArray modelJ=new JSONArray();
        JSONArray prJ=new JSONArray();
        JSONArray viewJ=new JSONArray();

        modelJ.addAll(matrixToArray(model));
        viewJ.addAll(matrixToArray(view));
        prJ.addAll(matrixToArray(projection));

        matrices.put("model",modelJ);
        matrices.put("view",viewJ);
        matrices.put("projection",prJ);

        dataToSave.put("matrices",matrices);
        try (FileWriter file=new FileWriter("data.json")){
            file.write(dataToSave.toJSONString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<Float> matrixToArray(Matrix4f matrix4f){
        float[] modelA=new float[16];
        matrix4f.get(modelA);
        ArrayList<Float> res=new ArrayList<>();
        for (float f:modelA) res.add(f);
        return res;
    }

    @SuppressWarnings("unchecked")
    private void loadMatrices(Matrix4f model,Matrix4f view,Matrix4f projection,JSONObject source){
        ArrayList<Double> modelInfo=(ArrayList<Double>)source.get("model");
        ArrayList<Double> viewInfo=(ArrayList<Double>)source.get("view");
        ArrayList<Double> projectionInfo=(ArrayList<Double>)source.get("projection");
        float[] modelA=new float[16];
        float[] viewA=new float[16];
        float[] projectionA=new float[16];
        int i=0;
        for (Double d:modelInfo) modelA[i++]=d.floatValue();
        i=0;
        for (Double d:viewInfo) viewA[i++]=d.floatValue();
        i=0;
        for (Double d:projectionInfo) projectionA[i++]=d.floatValue();
        model.set(modelA);
        view.set(viewA);
        projection.set(projectionA);
    }

    private void load(Cylinder c,Matrix4f model,Matrix4f view,Matrix4f projection){
        JSONParser parser=new JSONParser();
        try{
            JSONObject parseResult=(JSONObject) parser.parse(new FileReader("data.json"));
            JSONObject cylinderData=(JSONObject) parseResult.get("Cylinder");
            JSONObject animationData=(JSONObject) parseResult.get("Animation");
            JSONObject matrices=(JSONObject) parseResult.get("matrices");
            loadMatrices(model,view,projection,matrices);
            c.load(cylinderData,animationData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setUniforms(Shader shader){
        shader.setUniform("sampler",0);
        shader.setUniform("basementSampler",1);
        shader.setUniform("isBasement",0);
        shader.setUniform("isTextured",1);

        shader.setUniform("light.ambient",0.3f,0.3f,0.3f);
        shader.setUniform("light.diffuse",0.6f,0.6f,0.6f);
        shader.setUniform("light.specular",0.1f,0.1f,0.1f);

        shader.setUniform("material.ambient",0.55f,0.55f,0.55f);
        shader.setUniform("material.diffuse",0.3f,0.3f,0.3f);
        shader.setUniform("material.specular",0.15f,0.15f,0.15f);
        shader.setUniform("material.shininess",10);
    }

    private void input(Cylinder c,Matrix4f model,Matrix4f view,int[] z){
        if (glfwGetKey(window,GLFW_KEY_W)==1){
            view.translate(0,5f,0);
        }
        if (glfwGetKey(window,GLFW_KEY_A)==1){
            view.translate(-5f,0,0);
        }
        if (glfwGetKey(window,GLFW_KEY_S)==1){
            view.translate(0,-5f,0);
        }
        if (glfwGetKey(window,GLFW_KEY_D)==1){
            view.translate(5f,0,0);
        }

        if (glfwGetKey(window,GLFW_KEY_X)==1){
            model.rotate(0.05f,1,0,0);
        }
        if (glfwGetKey(window,GLFW_KEY_Y)==1){
            model.rotate(0.05f,0,1,0);
        }
        if (glfwGetKey(window,GLFW_KEY_Z)==1){
            model.rotate(0.05f,0,0,1);
        }

        if (glfwGetKey(window,GLFW_KEY_UP)==1) {
            z[1] += 2;
        }
        if (glfwGetKey(window,GLFW_KEY_LEFT)==1) {
            z[0] -= 2;
        }
        if (glfwGetKey(window,GLFW_KEY_DOWN)==1) {
            z[1] -= 2;
        }
        if (glfwGetKey(window,GLFW_KEY_RIGHT)==1) {
            z[0] += 2;
        }

        if (glfwGetKey(window,GLFW_KEY_Q)==1) {
            z[2] += 2;
        }
        if (glfwGetKey(window,GLFW_KEY_P)==1) {
            z[2] -= 2;
        }

        if (glfwGetKey(window,GLFW_KEY_LEFT_CONTROL)==1) model.scale(1.02f);
        if (glfwGetKey(window,GLFW_KEY_SPACE)==1) model.scale(0.98f);

        if (glfwGetKey(window,GLFW_KEY_I)==1) if (c.getStep()<2.09) c.setStep(c.getStep()+0.01);
        if (glfwGetKey(window,GLFW_KEY_O)==1) if (c.getStep()>0.05) c.setStep(c.getStep()-0.01);
        if (glfwGetKey(window,GLFW_KEY_K)==1) if (c.getLineStep()<30) c.setLineStep(c.getLineStep()+1);
        if (glfwGetKey(window,GLFW_KEY_L)==1) if (c.getLineStep()>1) c.setLineStep(c.getLineStep()-1);

        if (glfwGetKey(window,GLFW_KEY_ESCAPE)==1){
            glfwSetWindowShouldClose(window,true);
        }
    }

}