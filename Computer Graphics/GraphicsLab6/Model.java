import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

import org.joml.Vector3d;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

abstract class  Model {
    abstract void draw() throws InterruptedException;
    int angleX = 0,angleY = 0,angleZ = 0;
    Vector3d centre=new Vector3d();
    void changeFlag(){}
}

class Cylinder extends Model{
    double a,b,height,step=0.1,dist,lineStep=10;
    private boolean flag=false,animationFlag=false,textureFlag=false;

    ArrayList<Vector3d> basementHigh =new ArrayList<>();
    ArrayList<Vector3d> sideDetails =new ArrayList<>();
    ArrayList<Vector3d> basementLow=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsLow=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsHigh=new ArrayList<>();

    ArrayList<Vector3d> basementHighC =new ArrayList<>();
    ArrayList<Vector3d> sideDetailsC =new ArrayList<>();
    ArrayList<Vector3d> basementLowC=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsLowC=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsHighC=new ArrayList<>();

    ArrayList<ArrayList<Vector3d>> layers=new ArrayList<>();
    ArrayList<ArrayList<Vector3d>> layersC=new ArrayList<>();

    private final Animation animation;

    float[] dif=new float[]{0.55f,0.55f,0.55f,1};
    float[] amb=new float[]{0.3f,0.3f,0.3f,1};
    float[] spec=new float[]{0.15f,0.15f,0.15f,1};

    Cylinder(Vector3d centre, double a,double b, double height,double dist) {
        this.centre = centre;
        this.a = a;
        this.b = b;
        this.height = height;
        this.dist = dist;
        calc();
        animation=new Animation(this);
    }

    public void setLineStep(double lineStep) {
        this.lineStep = lineStep;
        reCalc();
    }

    void rotate(double angleX,double angleY,double angleZ){
        this.angleX+=angleX;
        this.angleY+=angleY;
        this.angleZ+=angleZ;
    }


    public Animation getAnimation() {
        return animation;
    }

    public double getLineStep() {
        return lineStep;
    }

    void changeAnimationFlag(){
        animationFlag=!animationFlag;
    }

    void changeTextureFlag(){
        if (textureFlag){
            glDisable(GL_TEXTURE_2D);
        }else{
            glEnable(GL_TEXTURE_2D);
        }
        textureFlag=!textureFlag;
    }

    private void calc(){
        double height=this.height/2;
        double x,y,angle,pi2=2*PI;
        //tryCalc();
        newTryCalc();
        for (angle=0;angle<pi2;angle+=step){
            x = a * cos(angle);
            y = b * sin(angle);
            basementHigh.add(new Vector3d(x-dist, y , height));
            basementLow.add(new Vector3d(x,y,-height));
        }
        basementHigh.add(new Vector3d(a-dist, 0, height));
        basementLow.add(new Vector3d(a,0,-height));

        newBasementCalc(basementDetailsHigh,basementHigh,-dist,0,height);
        newBasementCalc(basementDetailsLow,basementLow,0,0,-height);

        makeCopy();
    }


    private void makeCopy(){
        for (Vector3d sideDetail : sideDetails) {
            sideDetailsC.add(new Vector3d(sideDetail));
        }
        for (Vector3d sideDetail : basementLow) {
            basementLowC.add(new Vector3d(sideDetail));
        }
        for (Vector3d sideDetail : basementHigh) {
            basementHighC.add(new Vector3d(sideDetail));
        }
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

    private void newBasementCalc(ArrayList<Vector3d> result,ArrayList<Vector3d> source,double x,double y,double z){
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
            result.add(quoter1);
            result.add(half1);
            result.add(half2);
            result.add(quoter2);
            result.add(half1);
            result.add(third1);
            result.add(third2);
            result.add(half2);
            result.add(third1);
            result.add(third2);
            result.add(end2);
            result.add(end1);
        }
    }
    private void newTryCalc(){
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
    }

    protected void reCalc(){
        basementHigh.clear();
        basementDetailsLow.clear();
        basementDetailsHigh.clear();
        basementLow.clear();
        basementDetailsLowC.clear();
        basementDetailsHighC.clear();
        basementHighC.clear();
        basementLowC.clear();
        layers.clear();
        layersC.clear();
        calc();
    }

    double getStep() {
        return step;
    }

    public Vector3d getCentre() {
        return centre;
    }

    void setStep(double step) {
        this.step = step;
        reCalc();
    }

    void changeFlag(){
        flag=!flag;
    }

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
        JSONArray diffuse=new JSONArray(),ambient=new JSONArray(),specular=new JSONArray();
        for (int i=0;i<3;i++){
            diffuse.add(dif[i]);
            ambient.add(amb[i]);
            specular.add(spec[i]);
        }
        object.put("dif",diffuse);
        object.put("amb",ambient);
        object.put("spec",specular);

        addToJSON(basementDetailsLow,"basementDetailsLow",object);
        addToJSON(basementDetailsHigh,"basementDetailsHigh",object);

        addToJSON(basementDetailsLowC,"basementDetailsLowC",object);
        addToJSON(basementDetailsHighC,"basementDetailsHighC",object);

        JSONArray layer=new JSONArray();
        for (int i=0;i<layers.size();i++){
            for (int j=0;j<layers.get(i).size();j++){
                layer.add(layers.get(i).get(j).x);
                layer.add(layers.get(i).get(j).y);
                layer.add(layers.get(i).get(j).z);
            }

        }

        JSONArray layerC=new JSONArray();
        for (int i=0;i<layersC.size();i++){
            for (int j=0;j<layersC.get(i).size();j++){
                layerC.add(layersC.get(i).get(j).x);
                layerC.add(layersC.get(i).get(j).y);
                layerC.add(layersC.get(i).get(j).z);
            }
        }

        object.put("layers",layer);
        object.put("layersC",layerC);
        object.put("layersWidth",layers.get(0).size());
        object.put("layersHeight",layers.size());

        JSONObject res=new JSONObject();
        res.put("Cylinder",object);

        return res;
    }

    private void addToJSON(ArrayList<Vector3d> source,String name,JSONObject dest){
        JSONArray array=new JSONArray();
        for (Vector3d v:source){
            array.add(v.x);
            array.add(v.y);
            array.add(v.z);
        }
        dest.put(name,array);
    }

    private void getFromJSON(ArrayList<Vector3d> dest,String name,JSONObject source){
        ArrayList<Double> list=(ArrayList<Double>) source.get(name);
        for (int i=0,j=0;i<list.size();i+=3,j++){
            dest.get(j).x=list.get(i);
            dest.get(j).y=list.get(i+1);
            dest.get(j).z=list.get(i+2);
        }
    }

    void load(JSONObject cylinderData,JSONObject animationData){
        loadCylinder(cylinderData);
        animation.load(animationData);
    }

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
        angleX= ((Long) (dataToLoad.get("angleX"))).intValue();
        angleY=((Long) (dataToLoad.get("angleY"))).intValue();
        angleZ=((Long) (dataToLoad.get("angleZ"))).intValue();

        ArrayList<Double> pos=(ArrayList<Double>) dataToLoad.get("centre");
        centre.x=pos.get(0);
        centre.y=pos.get(1);
        centre.z=pos.get(2);
        ArrayList<Double> diffuse=(ArrayList<Double>) dataToLoad.get("dif");
        ArrayList<Double> ambient=(ArrayList<Double>) dataToLoad.get("amb");
        ArrayList<Double> specular=(ArrayList<Double>) dataToLoad.get("spec");
        for (int i=0;i<3;i++){
            dif[i]=diffuse.get(i).floatValue();
            amb[i]=ambient.get(i).floatValue();
            spec[i]=specular.get(i).floatValue();
        }

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
    }

    void draw() throws InterruptedException {
        glPushMatrix();
        glTranslated(centre.x,centre.y,centre.z);
        glRotatef(angleX,1,0,0);
        glRotatef(angleY,0,1,0);
        glRotatef(angleZ,0,0,1);

        Texture.textureStorage.get(6).bind();
        if (flag){
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
            newDrawSide();
            brandNewBasementDraw();
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
        }else{
            newDrawSide();
            brandNewBasementDraw();
        }

        if (animationFlag) animation.animate();

        glPopMatrix();
    }

    private void newDrawSide(){
        glBegin(GL_QUADS);

        Vector3d p,q,n;

        for (int i=0;i<layers.size()-1;i++){
            ArrayList<Vector3d> first=layers.get(i),second=layers.get(i+1);
            for (int j=0;j<first.size();j++){
                p=second.get(j).sub(first.get(j),new Vector3d());
                q=first.get((j+1)%first.size()).sub(first.get(j),new Vector3d());
                n=p.cross(q).normalize();
                glNormal3d(-n.x,-n.y,-n.z);

                glVertex3d(first.get(j).x,first.get(j).y,first.get(j).z);
                glTexCoord2d(acos((first.get(j).x+dist/2/(height/2)*(first.get(j).z+height/2))/a)/2/PI,first.get(j).z/height+0.5);

                //glNormal3d(-n.x,-n.y,-n.z);
                glVertex3d(second.get(j).x,second.get(j).y,second.get(j).z);
                glTexCoord2d(acos((second.get(j).x+dist/2/(height/2)*(second.get(j).z+height/2))/a)/2/PI,second.get(j).z/height+0.5);

                //glNormal3d(-n.x,-n.y,-n.z);
                glVertex3d(second.get((j+1)%first.size()).x,second.get((j+1)%first.size()).y,second.get((j+1)%first.size()).z);
                glTexCoord2d(acos((second.get((j+1)%first.size()).x+dist/2/(height/2)*(second.get((j+1)%first.size()).z+height/2))/a)/2/PI,
                        second.get((j+1)%first.size()).z/height+0.5);

                //glNormal3d(-n.x,-n.y,-n.z);
                glVertex3d(first.get((j+1)%first.size()).x,first.get((j+1)%first.size()).y,first.get((j+1)%first.size()).z);
                glTexCoord2d(acos((first.get((j+1)%first.size()).x+dist/2/(height/2)*(first.get((j+1)%first.size()).z+height/2))/a)/2/PI,
                        first.get((j+1)%first.size()).z/height+0.5);
            }
        }
        glEnd();
    }

    void move(float x,float y,float z){
        centre.add(x,y,z);
    }

    private void newBasementDraw(ArrayList<Vector3d> basementDetails,double c){
        Vector3d p,q,n;
        double bias=0;
        if (c==1) bias=dist;

        Texture.textureStorage.get(3).bind();
        for (int i=0;i<basementDetails.size();i+=15){
            glBegin(GL_TRIANGLES);
            p=basementDetails.get(i+1).sub(basementDetails.get(i),new Vector3d());
            q=basementDetails.get(i+2).sub(basementDetails.get(i),new Vector3d());
            n=p.cross(q);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i).x,basementDetails.get(i).y,basementDetails.get(i).z);
            glTexCoord2d(0.5,0.5);

            glVertex3d(basementDetails.get(i+1).x,basementDetails.get(i+1).y,basementDetails.get(i+1).z);
            glTexCoord2d(((basementDetails.get(i+1).x+bias)/a+1)/2,(basementDetails.get(i+1).y/b+1)/2);

            glVertex3d(basementDetails.get(i+2).x,basementDetails.get(i+2).y,basementDetails.get(i+2).z);
            glTexCoord2d(((basementDetails.get(i+2).x+bias)/a+1)/2,(basementDetails.get(i+2).y/b+1)/2);
            glEnd();

            glBegin(GL_POLYGON);
            p=basementDetails.get(i+4).sub(basementDetails.get(i+3),new Vector3d());
            q=basementDetails.get(i+6).sub(basementDetails.get(i+3),new Vector3d());
            n=p.cross(q);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i+3).x,basementDetails.get(i+3).y,basementDetails.get(i+3).z);
            glTexCoord2d(((basementDetails.get(i+3).x+bias)/a+1)/2,(basementDetails.get(i+3).y/b+1)/2);

            glVertex3d(basementDetails.get(i+4).x,basementDetails.get(i+4).y,basementDetails.get(i+4).z);
            glTexCoord2d(((basementDetails.get(i+4).x+bias)/a+1)/2,(basementDetails.get(i+4).y/b+1)/2);

            glVertex3d(basementDetails.get(i+5).x,basementDetails.get(i+5).y,basementDetails.get(i+5).z);
            glTexCoord2d(((basementDetails.get(i+5).x+bias)/a+1)/2,(basementDetails.get(i+5).y/b+1)/2);

            glVertex3d(basementDetails.get(i+6).x,basementDetails.get(i+6).y,basementDetails.get(i+6).z);
            glTexCoord2d(((basementDetails.get(i+6).x+bias)/a+1)/2,(basementDetails.get(i+6).y/b+1)/2);
            glEnd();

            glBegin(GL_POLYGON);
            p=basementDetails.get(i+8).sub(basementDetails.get(i+7),new Vector3d());
            q=basementDetails.get(i+10).sub(basementDetails.get(i+7),new Vector3d());
            n=p.cross(q);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i+7).x,basementDetails.get(i+7).y,basementDetails.get(i+7).z);
            glTexCoord2d(((basementDetails.get(i+7).x+bias)/a+1)/2,(basementDetails.get(i+7).y/b+1)/2);

            glVertex3d(basementDetails.get(i+8).x,basementDetails.get(i+8).y,basementDetails.get(i+8).z);
            glTexCoord2d(((basementDetails.get(i+8).x+bias)/a+1)/2,(basementDetails.get(i+8).y/b+1)/2);

            glVertex3d(basementDetails.get(i+9).x,basementDetails.get(i+9).y,basementDetails.get(i+9).z);
            glTexCoord2d(((basementDetails.get(i+9).x+bias)/a+1)/2,(basementDetails.get(i+9).y/b+1)/2);

            glVertex3d(basementDetails.get(i+10).x,basementDetails.get(i+10).y,basementDetails.get(i+10).z);
            glTexCoord2d(((basementDetails.get(i+10).x+bias)/a+1)/2,(basementDetails.get(i+10).y/b+1)/2);
            glEnd();

            glBegin(GL_POLYGON);
            p=basementDetails.get(i+12).sub(basementDetails.get(i+11),new Vector3d());
            q=basementDetails.get(i+14).sub(basementDetails.get(i+11),new Vector3d());
            n=p.cross(q);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i+11).x,basementDetails.get(i+11).y,basementDetails.get(i+11).z);
            glTexCoord2d(((basementDetails.get(i+11).x+bias)/a+1)/2,(basementDetails.get(i+11).y/b+1)/2);

            glVertex3d(basementDetails.get(i+12).x,basementDetails.get(i+12).y,basementDetails.get(i+12).z);
            glTexCoord2d(((basementDetails.get(i+12).x+bias)/a+1)/2,(basementDetails.get(i+12).y/b+1)/2);

            glVertex3d(basementDetails.get(i+13).x,basementDetails.get(i+13).y,basementDetails.get(i+13).z);
            glTexCoord2d(((basementDetails.get(i+13).x+bias)/a+1)/2,(basementDetails.get(i+13).y/b+1)/2);

            glVertex3d(basementDetails.get(i+14).x,basementDetails.get(i+14).y,basementDetails.get(i+14).z);
            glTexCoord2d(((basementDetails.get(i+14).x+bias)/a+1)/2,(basementDetails.get(i+14).y/b+1)/2);
            glEnd();
        }
    }


    private void brandNewBasementDraw(){
        newBasementDraw(basementDetailsHigh,1);
        newBasementDraw(basementDetailsLow,-1);
    }
}




