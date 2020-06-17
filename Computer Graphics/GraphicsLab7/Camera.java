import org.joml.Vector3d;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Camera {
    private final Vector3d position;
    private double angleX,angleY,angleZ;
    private double distance=50;
    Camera(double x,double y,double z){
        position=new Vector3d();
    }

    void move(double x,double y,double z){
        position.add(x,y,z);
        distance+=z/10;
    }

    void rotate(double angleX,double angleY,double angleZ){
        this.angleX+=angleX;
        this.angleY+=angleY;
        this.angleZ+=angleZ;
    }

    void render(){
        glTranslated(position.x,position.y,position.z);

        glRotated(angleX,1,0,0);
        glRotated(angleY,0,1,0);
        glRotated(angleZ,0,0,1);
        double s=50/distance;
        glScaled(s,s,s);
    }

    JSONObject toJSON(){
        JSONObject object=new JSONObject();
        JSONArray pos=new JSONArray();
        pos.add(position.x);
        pos.add(position.y);
        pos.add(position.z);
        object.put("position",pos);
        object.put("angleX",angleX);
        object.put("angleY",angleY);
        object.put("angleZ",angleZ);
        object.put("distance",distance);
        JSONObject result=new JSONObject();
        result.put("Camera",object);
        return result;
    }

    void load(JSONObject dataToLoad){
        ArrayList<Double> pos=(ArrayList<Double>)dataToLoad.get("position");
        position.x=pos.get(0);
        position.y=pos.get(1);
        position.z=pos.get(2);
        angleX=(double)dataToLoad.get("angleX");
        angleY=(double)dataToLoad.get("angleY");
        angleZ=(double)dataToLoad.get("angleZ");
        distance=(double)dataToLoad.get("distance");
    }
}

