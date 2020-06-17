import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lwjgl.BufferUtils;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

public class Cylinder {
    double a,b,height,step=0.1,dist,lineStep=10;
    private boolean flag=false,animationFlag=false,textureFlag=true;
    Vector3f centre;

    float angleX,angleY,angleZ;

    private int sideVao, bHVao, bLVao;

    ArrayList<Vector3d> side=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsHigh=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsLow=new ArrayList<>();
    ArrayList<ArrayList<Vector3d>> layers=new ArrayList<>();

    ArrayList<Vector3d> basementDetailsHighC=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsLowC=new ArrayList<>();
    ArrayList<ArrayList<Vector3d>> layersC=new ArrayList<>();

    ArrayList<Vector2d> sideTexCoords =new ArrayList<>();
    ArrayList<Vector2d> basementTexDetailsHigh=new ArrayList<>();
    ArrayList<Vector2d> basementTexDetailsLow=new ArrayList<>();

    ArrayList<Vector3d> sideNormals=new ArrayList<>();
    ArrayList<Vector3d> basementNormalsHigh=new ArrayList<>();
    ArrayList<Vector3d> basementNormalsLow=new ArrayList<>();

    private Shader shader;
    private final Animation animation;

    Cylinder(Vector3f centre, double a, double b, double height, double dist){
        this.centre = centre;
        this.a = a;
        this.b = b;
        this.height = height;
        this.dist = dist;
        calc();
        animation=new Animation(this);
    }

    void changeFlag(){
        flag=!flag;
    }

    void changeAnimationFlag(){
        animationFlag=!animationFlag;
    }

    void changeTextureFlag(){
        textureFlag=!textureFlag;
        if (textureFlag){
            shader.setUniform("isTextured",1);
        }else{
            shader.setUniform("isTextured",0);
        }
    }

    void setShader(Shader shader){
        this.shader=shader;
    }

    public double getStep() {
        return step;
    }

    public double getLineStep() {
        return lineStep;
    }

    Animation getAnimation(){
        return animation;
    }

    public void setStep(double step) {
        if (!animation.inProcess()) {
            this.step = step;
            reCalc();
        }
    }

    public void setLineStep(double lineStep) {
        if (!animation.inProcess()) {
            this.lineStep = lineStep;
            reCalc();
        }
    }

    private DoubleBuffer toBuffer3d(ArrayList<Vector3d> list){
        DoubleBuffer buffer= BufferUtils.createDoubleBuffer(list.size()*3);
        for (Vector3d v:list){
            buffer.put(v.x);
            buffer.put(v.y);
            buffer.put(v.z);
        }
        buffer.flip();
        return buffer;
    }

    private DoubleBuffer toBuffer2d(ArrayList<Vector2d> list){
        DoubleBuffer buffer= BufferUtils.createDoubleBuffer(list.size()*2);
        for (Vector2d v:list){
            buffer.put(v.x);
            buffer.put(v.y);
        }
        buffer.flip();
        return buffer;
    }

    private void loadVao(){
        sideVao();
        basementVao();
    }

    private void storeVbo(int index,int size,DoubleBuffer data){
        int vbo=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vbo);
        glBufferData(GL_ARRAY_BUFFER,data,GL_STATIC_DRAW);
        glVertexAttribPointer(index,size,GL_DOUBLE,false,0,0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    private void sideVao(){
        sideVao =glGenVertexArrays();
        glBindVertexArray(sideVao);
        storeVbo(0,3, toBuffer3d(side));
        storeVbo(1,2,toBuffer2d(sideTexCoords));
        storeVbo(2,3, toBuffer3d(sideNormals));
        glBindVertexArray(0);
    }

    private void basementVao(){
        bHVao =glGenVertexArrays();
        glBindVertexArray(bHVao);
        storeVbo(0,3, toBuffer3d(basementDetailsHigh));
        storeVbo(1,2,toBuffer2d(basementTexDetailsHigh));
        storeVbo(2,3, toBuffer3d(basementNormalsHigh));
        glBindVertexArray(0);
        bLVao =glGenVertexArrays();
        glBindVertexArray(bLVao);
        storeVbo(0,3, toBuffer3d(basementDetailsLow));
        storeVbo(1,2,toBuffer2d(basementTexDetailsLow));
        storeVbo(2,3, toBuffer3d(basementNormalsLow));
        glBindVertexArray(0);
    }


    private void basementCalc(ArrayList<Vector3d> result,ArrayList<Vector2d> tex, ArrayList<Vector3d> source, double x, double y, double z){
        Vector3d help=new Vector3d(0.5,0.5,1);
        for (int i = 0; i< source.size(); i++){
            Vector3d start=new Vector3d(x,y,z);
            Vector3d end1= source.get(i);
            Vector3d end2= source.get(i+1 == source.size() ? 0 : i+1);
            Vector3d half1=end1.add(start,new Vector3d()).mul(help);
            Vector3d half2=end2.add(start,new Vector3d()).mul(help);
            Vector3d quoter1=half1.add(start,new Vector3d()).mul(help);
            Vector3d quoter2=half2.add(start,new Vector3d()).mul(help);
            Vector3d third1=end1.add(half1,new Vector3d()).mul(help);
            Vector3d third2=end2.add(half2,new Vector3d()).mul(help);
            start.z=z;
            quoter1.z=quoter2.z=z;
            half1.z=half2.z=z;
            third1.z=third2.z=z;
            end1.z=end2.z=z;
            result.add(start);
            result.add(quoter1);
            result.add(quoter2);
            result.add(start);
            tex.add(new Vector2d(0.5,0.5));
            tex.add(new Vector2d((4*(quoter1.x-x)/a+1)/2,(4*quoter1.y/b+1)/2));
            tex.add(new Vector2d((4*(quoter2.x-x)/a+1)/2,(4*quoter2.y/b+1)/2));
            tex.add(new Vector2d(0.5,0.5));

            result.add(quoter1);
            result.add(half1);
            result.add(half2);
            result.add(quoter2);
            tex.add(new Vector2d((4*(quoter1.x-x)/a+1)/2,(4*quoter1.y/b+1)/2));
            tex.add(new Vector2d((2*(half1.x-x)/a+1)/2,(2*half1.y/b+1)/2));
            tex.add(new Vector2d((2*(half2.x-x)/a+1)/2,(2*half2.y/b+1)/2));
            tex.add(new Vector2d((4*(quoter2.x-x)/a+1)/2,(4*quoter2.y/b+1)/2));

            result.add(half1);
            result.add(third1);
            result.add(third2);
            result.add(half2);
            tex.add(new Vector2d((2*(half1.x-x)/a+1)/2,(2*half1.y/b+1)/2));
            tex.add(new Vector2d((4*(third1.x-x)/a/3+1)/2,(4*third1.y/b/3+1)/2));
            tex.add(new Vector2d((4*(third2.x-x)/a/3+1)/2,(4*third2.y/b/3+1)/2));
            tex.add(new Vector2d((2*(half2.x-x)/a+1)/2,(2*half2.y/b+1)/2));

            result.add(third1);
            result.add(end1);
            result.add(end2);
            result.add(third2);
            tex.add(new Vector2d((4*(third1.x-x)/a/3+1)/2,(4*third1.y/b/3+1)/2));
            tex.add(new Vector2d(((end1.x-x)/a+1)/2,(end1.y/b+1)/2));
            tex.add(new Vector2d(((end2.x-x)/a+1)/2,(end1.y/b+1)/2));
            tex.add(new Vector2d((4*(third2.x-x)/a/3+1)/2,(4*third2.y/b/3+1)/2));
        }
    }

    private ArrayList<Vector3d> addBasementNormals(ArrayList<Vector3d> basement,int c){
        Vector3d p=basement.get(1).sub(basement.get(0),new Vector3d());
        Vector3d q=basement.get(2).sub(basement.get(0),new Vector3d());
        Vector3d n=p.cross(q).mul(c);
        ArrayList<Vector3d> result=new ArrayList<>();
        for (int i=0;i<basement.size();i++){
            result.add(n);
        }
        return result;
    }

    private void sideCalc(){
        double height = this.height / 2;
        double x,y,angle, pi2 = 2 * PI;
        for (double z = -height; z < height; z += lineStep) {
            ArrayList<Vector3d> list=new ArrayList<>();
            for (angle = 0; angle < pi2; angle += step) {
                x = a * cos(angle);
                y = b * sin(angle);
                if (z==-height) list.add(new Vector3d(x,y,z));
                else list.add(new Vector3d(-dist/2./height*(z+height)+x,y,z));

            }
            list.add(new Vector3d(-dist/2./height*(z+height)+a,0,z));
            layers.add(list);
        }

        ArrayList<Vector3d> list=new ArrayList<>();
        for (angle = 0; angle < pi2; angle += step) {
            x = a * cos(angle);
            y = b * sin(angle);
            list.add(new Vector3d(-dist/2./height*(height+height)+x,y,height));

        }
        list.add(new Vector3d(-dist/2./height*(height+height)+a,0,height));
        layers.add(list);


        for (int i=0;i<layers.size()-1;i++){
            ArrayList<Vector3d> first=layers.get(i),second=layers.get(i+1);
            for (int j=0;j<first.size();j++){
                side.add(first.get(j));
                side.add(second.get(j));
                side.add(second.get((j+1)%first.size()));
                side.add(first.get((j+1)%first.size()));
            }
        }
        sideTextureCalc();
    }

    private void makeCopy(){
        for (Vector3d sideDetail : basementDetailsLow) {
            basementDetailsLowC.add(new Vector3d(sideDetail));
        }
        for (Vector3d sideDetail : basementDetailsHigh) {
            basementDetailsHighC.add(new Vector3d(sideDetail));
        }
        for (ArrayList<Vector3d> arrayList:layers){
            ArrayList<Vector3d> list=new ArrayList<>();
            for (Vector3d vec:arrayList) list.add(new Vector3d(vec));
            layersC.add(list);
        }
    }

    private void sideTextureCalc(){
        Vector3d p,q;
        for (int i=0;i<layers.size()-1;i++){
            ArrayList<Vector3d> first=layers.get(i),second=layers.get(i+1);
            for (int j=0;j<first.size();j++){
                p=second.get(j).sub(first.get(j),new Vector3d());
                q=first.get((j+1)%first.size()).sub(first.get(j),new Vector3d());
                Vector3d n=q.cross(p);
                sideNormals.add(n);
                sideTexCoords.add(new Vector2d(acos((first.get(j).x+dist/2/(height/2)*(first.get(j).z+height/2))/a)/2/PI,
                        first.get(j).z/height+0.5));

                sideNormals.add(n);
                sideTexCoords.add(new Vector2d(acos((second.get(j).x+dist/2/(height/2)*(second.get(j).z+height/2))/a)/2/PI,
                        second.get(j).z/height+0.5));

                sideNormals.add(n);
                sideTexCoords.add(new Vector2d(
                        acos((second.get((j+1)%first.size()).x+dist/2/(height/2)*(second.get((j+1)%first.size()).z+height/2))/a) /2/PI,
                        second.get((j+1)%first.size()).z/height+0.5)
                            );

                sideNormals.add(n);
                sideTexCoords.add(new Vector2d(
                        acos((first.get((j+1)%first.size()).x+dist/2/(height/2)*(first.get((j+1)%first.size()).z+height/2))/a)/2/PI,
                        first.get((j+1)%first.size()).z/height+0.5)
                            );
            }
        }
    }

    private void calc(){
        sideCalc();
        basementCalc(basementDetailsHigh,basementTexDetailsHigh,layers.get(layers.size()-1),-dist,0,height/2);
        basementCalc(basementDetailsLow,basementTexDetailsLow,layers.get(0),0,0,-height/2);
        basementNormalsHigh=addBasementNormals(basementDetailsHigh,1);
        basementNormalsLow=addBasementNormals(basementDetailsLow,-1);
        loadVao();
        makeCopy();
    }

    private void reCalc(){
        side.clear();
        basementDetailsHigh.clear();
        basementDetailsLow.clear();
        layers.clear();
        sideTexCoords.clear();
        basementTexDetailsHigh.clear();
        basementTexDetailsLow.clear();
        basementDetailsLowC.clear();
        basementDetailsHighC.clear();
        layersC.clear();
        sideNormals.clear();
        calc();
    }

    void draw(){
        if (flag){
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
            sideDraw();
            basementDraw();
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
        }else {
            sideDraw();
            basementDraw();
        }

        if (animationFlag) {
            animation.animate();
            loadVao();
        }
    }

    void sideDraw(){
        shader.setUniform("isBasement",0);
        Texture.textureStorage.get(6).bind(1);
        glBindVertexArray(sideVao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawArrays(GL_QUADS,0,side.size());
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }


    void basementDraw(){
        shader.setUniform("isBasement",1);
        Texture.textureStorage.get(4).bind(0);
        glBindVertexArray(bHVao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawArrays(GL_QUADS,0,basementDetailsHigh.size());
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        glBindVertexArray(bLVao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawArrays(GL_QUADS,0,basementDetailsLow.size());
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    @SuppressWarnings("unchecked")
    JSONObject toJSON(){
        JSONObject object=new JSONObject();
        object.put("a",a);
        object.put("b",b);
        object.put("height",height);
        object.put("dist",dist);
        object.put("flag",flag);
        object.put("animationFlag",animationFlag);
        object.put("lineStep",lineStep);
        object.put("step",step);
        object.put("textureFlag",textureFlag);
        object.put("angleX",angleX);
        object.put("angleY",angleY);
        object.put("angleZ",angleZ);
        JSONArray pos=new JSONArray();
        pos.add(centre.x);
        pos.add(centre.y);
        pos.add(centre.z);
        object.put("centre",pos);

        addToJSON(basementDetailsLow,"basementDetailsLow",object);
        addToJSON(basementDetailsHigh,"basementDetailsHigh",object);

        addToJSON(basementDetailsLowC,"basementDetailsLowC",object);
        addToJSON(basementDetailsHighC,"basementDetailsHighC",object);

        addToJSON(side,"side",object);


        JSONArray layer=new JSONArray();
        for (ArrayList<Vector3d> list : layers) {
            for (Vector3d vector3d : list) {
                layer.add(vector3d.x);
                layer.add(vector3d.y);
                layer.add(vector3d.z);
            }

        }

        JSONArray layerC=new JSONArray();
        for (ArrayList<Vector3d> list : layersC) {
            for (Vector3d vector3d : list) {
                layerC.add(vector3d.x);
                layerC.add(vector3d.y);
                layerC.add(vector3d.z);
            }
        }
        
        JSONArray texB=new JSONArray();
        for (Vector2d sideTexCoord : basementTexDetailsLow) {
            texB.add(sideTexCoord.x);
            texB.add(sideTexCoord.y);
        }

        object.put("basementTexDetailsLow",texB);

        JSONArray texB2=new JSONArray();
        for (Vector2d sideTexCoord : basementTexDetailsHigh) {
            texB2.add(sideTexCoord.x);
            texB2.add(sideTexCoord.y);
        }

        object.put("basementTexDetailsHigh",texB2);

        object.put("layers",layer);
        object.put("layersC",layerC);
        object.put("layersWidth",layers.get(0).size());
        object.put("layersHeight",layers.size());

        JSONObject res=new JSONObject();
        res.put("Cylinder",object);

        return res;
    }

    @SuppressWarnings("unchecked")
    private void addToJSON(ArrayList<Vector3d> source,String name,JSONObject dest){
        JSONArray array=new JSONArray();
        for (Vector3d v:source){
            array.add(v.x);
            array.add(v.y);
            array.add(v.z);
        }
        dest.put(name,array);
    }

    @SuppressWarnings("unchecked")
    private void getFromJSON(ArrayList<Vector3d> dest,String name,JSONObject source){
        ArrayList<Double> list=(ArrayList<Double>) source.get(name);
        dest.clear();
        for (int i=0;i<list.size();i+=3){
            dest.add(new Vector3d(list.get(i),list.get(i+1),list.get(i+2)));
        }
    }

    void load(JSONObject cylinderData, JSONObject animationData){
        loadCylinder(cylinderData);
        animation.load(animationData);
    }

    @SuppressWarnings("unchecked")
    private void loadCylinder(JSONObject dataToLoad) {
        a = (double) dataToLoad.get("a");
        b = (double) dataToLoad.get("b");
        height = (double) dataToLoad.get("height");
        dist = (double) dataToLoad.get("dist");
        flag = (boolean) dataToLoad.get("flag");
        animationFlag = (boolean) dataToLoad.get("animationFlag");
        lineStep = (double) dataToLoad.get("lineStep");
        step = (double) dataToLoad.get("step");
        if ((boolean) dataToLoad.get("textureFlag") !=textureFlag){
            changeTextureFlag();
        }
        angleX= ((Double) (dataToLoad.get("angleX"))).floatValue();
        angleY=((Double) (dataToLoad.get("angleY"))).floatValue();
        angleZ=((Double) (dataToLoad.get("angleZ"))).floatValue();

        ArrayList<Double> pos=(ArrayList<Double>) dataToLoad.get("centre");
        centre.x=pos.get(0).floatValue();
        centre.y=pos.get(1).floatValue();
        centre.z=pos.get(2).floatValue();

        getFromJSON(basementDetailsLow, "basementDetailsLow", dataToLoad);
        getFromJSON(basementDetailsHigh, "basementDetailsHigh", dataToLoad);

        getFromJSON(basementDetailsLowC, "basementDetailsLowC", dataToLoad);
        getFromJSON(basementDetailsHighC, "basementDetailsHighC", dataToLoad);


        ArrayList<Double> layer=(ArrayList<Double>) dataToLoad.get("layers");
        int w=((Long) dataToLoad.get("layersWidth")).intValue();
        int h=((Long) dataToLoad.get("layersHeight")).intValue();

        layers.clear();
        for (int i=0;i<h;i++) {
            ArrayList<Vector3d> array=new ArrayList<>();
            for (int j=0;j<w;j++){
                array.add(new Vector3d(layer.get((i*w+j)*3),layer.get((i*w+j)*3+1),layer.get((i*w+j)*3+2)));
            }
            layers.add(array);
        }

        layersC.clear();
        layer=(ArrayList<Double>) dataToLoad.get("layersC");
        for (int i=0;i<h;i++) {
            ArrayList<Vector3d> array=new ArrayList<>();
            for (int j=0;j<w;j++){
                array.add(new Vector3d(layer.get((i*w+j)*3),layer.get((i*w+j)*3+1),layer.get((i*w+j)*3+2)));
            }
            layersC.add(array);
        }

        side.clear();
        for (int i=0;i<layers.size()-1;i++){
            ArrayList<Vector3d> first=layers.get(i),second=layers.get(i+1);
            for (int j=0;j<first.size();j++){
                side.add(first.get(j));
                side.add(second.get(j));
                side.add(second.get((j+1)%first.size()));
                side.add(first.get((j+1)%first.size()));
            }
        }

        ArrayList<Double> texB=(ArrayList<Double>) dataToLoad.get("basementTexDetailsLow");
        basementTexDetailsLow.clear();
        for (int i=0;i<texB.size();i+=2){
            basementTexDetailsLow.add(new Vector2d(texB.get(i),texB.get(i+1)));
        }

        ArrayList<Double> texB2=(ArrayList<Double>) dataToLoad.get("basementTexDetailsHigh");
        basementTexDetailsHigh.clear();
        for (int i=0;i<texB2.size();i+=2){
            basementTexDetailsHigh.add(new Vector2d(texB2.get(i),texB2.get(i+1)));
        }

        loadVao();
    }

}
