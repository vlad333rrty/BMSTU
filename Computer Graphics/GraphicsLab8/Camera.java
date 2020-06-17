import org.joml.Vector3d;
import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Camera {
    private final Vector3f position;
    float angleX,angleY,angleZ;
    private double distance=50;
    Camera(double x,double y,double z){
        position=new Vector3f();
    }

    void move(float x,float y,float z){
        position.add(x,y,z);
        distance+=z/10;
    }

    void render(){
        glTranslated(position.x,position.y,position.z);
        glRotated(angleX,1,0,0);
        glRotated(angleY,0,1,0);
        glRotated(angleZ,0,0,1);
    }

    void rotate(float angleX,float angleY,float angleZ){
        this.angleX+=angleX;
        this.angleY+=angleY;
        this.angleZ+=angleZ;
    }

    float getZoom(){
        return (float)(50/distance);
    }

    Vector3f getPosition(){
        return position;
    }

}

