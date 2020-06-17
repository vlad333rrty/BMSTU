import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

abstract class Model{
    abstract void draw() throws InterruptedException;
    int angleX = 0,angleY = 0,angleZ = 0;
    Vector3d centre=new Vector3d();
    void changeFlag(){}
}

class Cylinder extends Model{
    double a,b,height,step=0.1,dist,lineStep=10;
    private boolean flag=false,animationFlag=false,textureFlag=true;

    ArrayList<Vector3d> basementDetailsLow=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsHigh=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsLowC=new ArrayList<>();
    ArrayList<Vector3d> basementDetailsHighC=new ArrayList<>();

    double[][] basementDetailsLowD;
    double[][] basementDetailsHighD;

    ArrayList<ArrayList<Vector3d>> layers=new ArrayList<>();
    ArrayList<ArrayList<Vector3d>> layersC=new ArrayList<>();

    ArrayList<ArrayList<Vector2d>> texCoords=new ArrayList<>();
    private final ArrayList<Vector3d> sideNormals=new ArrayList<>();
    private int id,id1,id2,idNoT;

    private final Animation animation;
    private double lineStepPrev,stepPrev;

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
        if (!animationFlag){
            lineStepPrev=lineStep;
            stepPrev=step;
            step=0.5;
            lineStep=15;
            reCalc();
        }
        animationFlag=!animationFlag;
    }

    private double[] toArray(Vector3d vec){
        return new double[]{vec.x,vec.y,vec.z};
    }

    void loadPrev(){
        if (stepPrev!=0) step=stepPrev;
        if (lineStepPrev!=0) lineStep=lineStepPrev;
        reCalc();
    }

    void changeTextureFlag(){
        if (textureFlag){
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_NORMALIZE);
        }else{
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_NORMALIZE);
            glShadeModel(GL_FLAT);
        }
        textureFlag=!textureFlag;
    }

    private void calc(){
        double height=this.height/2;
        double x,y,angle,pi2=2*PI;
        sideCalc();

        ArrayList<Vector3d> basementHigh=new ArrayList<>();
        ArrayList<Vector3d> basementLow=new ArrayList<>();
        for (angle=0;angle<pi2;angle+=step){
            x = a * cos(angle);
            y = b * sin(angle);
            basementHigh.add(new Vector3d(x-dist, y , height));
            basementLow.add(new Vector3d(x,y,-height));
        }
        basementHigh.add(new Vector3d(a-dist, 0, height));
        basementLow.add(new Vector3d(a,0,-height));

        basementCalc(basementDetailsHigh,basementHigh,-dist,0,height);
        basementCalc(basementDetailsLow,basementLow,0,0,-height);

        basementDetailsHighD=new double[basementDetailsHigh.size()][3];
        basementDetailsLowD=new double[basementDetailsLow.size()][3];

        for (int i=0;i<basementDetailsHigh.size();i++){
            basementDetailsHighD[i]=toArray(basementDetailsHigh.get(i));
        }
        for (int i=0;i<basementDetailsLow.size();i++){
            basementDetailsLowD[i]=toArray(basementDetailsLow.get(i));
        }

        textureCalc();

        makeCopy();

        drawList();
        drawNoTList();
        basementList(basementDetailsHigh,dist,basementDetailsHighD);
        basementList(basementDetailsLow,0,basementDetailsLowD);
    }


    private void textureCalc(){
        Vector3d p,q;
        for (int i=0;i<layers.size()-1;i++){
            ArrayList<Vector3d> first=layers.get(i),second=layers.get(i+1);
            ArrayList<Vector2d> list=new ArrayList<>();
            for (int j=0;j<first.size();j++){
                p=second.get(j).sub(first.get(j),new Vector3d());
                q=first.get((j+1)%first.size()).sub(first.get(j),new Vector3d());
                Vector3d n=p.cross(q);
                sideNormals.add(n);
                list.add(new Vector2d(acos((first.get(j).x+dist/2/(height/2)*(first.get(j).z+height/2))/a)/PI,
                        first.get(j).z/height+0.5));

                list.add(new Vector2d(acos((second.get(j).x+dist/2/(height/2)*(second.get(j).z+height/2))/a)/PI,
                        second.get(j).z/height+0.5));


                list.add(new Vector2d(acos((second.get((j+1)%first.size()).x+dist/2/(height/2)*(second.get((j+1)%first.size()).z+height/2))/a)/PI,
                        second.get((j+1)%first.size()).z/height+0.5));


                list.add(new Vector2d(acos((first.get((j+1)%first.size()).x+dist/2/(height/2)*(first.get((j+1)%first.size()).z+height/2))/a)/PI,
                        first.get((j+1)%first.size()).z/height+0.5));
            }
            texCoords.add(list);
        }
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

    private void basementCalc(ArrayList<Vector3d> result, ArrayList<Vector3d> source, double x, double y, double z){
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
            result.add(end1);
            result.add(end2);
            result.add(third2);
        }
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
    }

    protected void reCalc(){
        basementDetailsLow.clear();
        basementDetailsHigh.clear();
        basementDetailsLowC.clear();
        basementDetailsHighC.clear();
        layers.clear();
        layersC.clear();
        texCoords.clear();
        sideNormals.clear();
        calc();
    }

    double getStep() {
        return step;
    }

    void setStep(double step) {
        this.step = step;
        reCalc();
    }

    void changeFlag(){
        flag=!flag;
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

    void draw(){
        glPushMatrix();
        glTranslated(centre.x,centre.y,centre.z);
        glRotatef(angleX,1,0,0);
        glRotatef(angleY,0,1,0);
        glRotatef(angleZ,0,0,1);

        if (flag){
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
            drawSide();
            basementDraw();
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
        }else{
            drawSide();
            basementDraw();
        }

        if (animationFlag) animation.animate();

        glPopMatrix();
    }

    private void drawList(){
        id=glGenLists(1);
        if (id!=0) {
            glNewList(id, GL_COMPILE);

            for (int i = 0; i < layers.size() - 1; i++) {
                ArrayList<Vector3d> first = layers.get(i), second = layers.get(i + 1);
                ArrayList<Vector2d> list = texCoords.get(i);
                for (int j = 0, k = 0; j < first.size(); j++, k += 4) {
                    glBegin(GL_QUADS);
                    glVertex3d(first.get(j).x, first.get(j).y, first.get(j).z);
                    glTexCoord2d(list.get(k).x, list.get(k).y);

                    glVertex3d(second.get(j).x, second.get(j).y, second.get(j).z);
                    glTexCoord2d(list.get(k + 1).x, list.get(k + 1).y);

                    glVertex3d(second.get((j + 1) % first.size()).x, second.get((j + 1) % first.size()).y,
                            second.get((j + 1) % first.size()).z);
                    glTexCoord2d(list.get(k + 2).x, list.get(k + 2).y);

                    glVertex3d(first.get((j + 1) % first.size()).x, first.get((j + 1) % first.size()).y,
                            first.get((j + 1) % first.size()).z);
                    glTexCoord2d(list.get(k + 3).x, list.get(k + 3).y);
                    glEnd();
                }
            }

            glEndList();
        }
    }

    private void drawNoTList(){
        Vector3d n;
        idNoT=glGenLists(1);
        if (idNoT!=0){
            glNewList(idNoT, GL_COMPILE);
            for (int i = 0; i < layers.size() - 1; i++) {
                ArrayList<Vector3d> first = layers.get(i), second = layers.get(i + 1);
                for (int j = 0, k = 0; j < first.size(); j++, k += 4) {
                    n = sideNormals.get(j + i * first.size());
                    glBegin(GL_QUADS);
                    glNormal3d(-n.x, -n.y, -n.z);
                    glVertex3d(first.get(j).x, first.get(j).y, first.get(j).z);
                    glVertex3d(second.get(j).x, second.get(j).y, second.get(j).z);
                    glVertex3d(second.get((j + 1) % first.size()).x, second.get((j + 1) % first.size()).y,
                            second.get((j + 1) % first.size()).z);
                    glVertex3d(first.get((j + 1) % first.size()).x, first.get((j + 1) % first.size()).y,
                            first.get((j + 1) % first.size()).z);
                    glEnd();
                }
            }
            glEndList();
        }
    }


    private void drawSide(){
        if (textureFlag) Texture.textureStorage.get(6).bind();
        if (animationFlag) {
            Vector3d n;
            for (int i = 0; i < layers.size() - 1; i++) {
                ArrayList<Vector3d> first = layers.get(i), second = layers.get(i + 1);
                ArrayList<Vector2d> list = texCoords.get(i);
                for (int j = 0, k = 0; j < first.size(); j++, k += 4) {
                    n = sideNormals.get(j + i * first.size());
                    glBegin(GL_QUADS);
                    if (!textureFlag) glNormal3d(-n.x, -n.y, -n.z);
                    glVertex3d(first.get(j).x, first.get(j).y, first.get(j).z);
                    glTexCoord2d(list.get(k).x, list.get(k).y);

                    glVertex3d(second.get(j).x, second.get(j).y, second.get(j).z);
                    glTexCoord2d(list.get(k + 1).x, list.get(k + 1).y);

                    glVertex3d(second.get((j + 1) % first.size()).x, second.get((j + 1) % first.size()).y,
                            second.get((j + 1) % first.size()).z);
                    glTexCoord2d(list.get(k + 2).x, list.get(k + 2).y);

                    glVertex3d(first.get((j + 1) % first.size()).x, first.get((j + 1) % first.size()).y,
                            first.get((j + 1) % first.size()).z);
                    glTexCoord2d(list.get(k + 3).x, list.get(k + 3).y);
                    glEnd();
                }
            }
        }else if (textureFlag){
            glCallList(id);
        }else{
            glCallList(idNoT);
        }

    }

    void move(float x,float y,float z){
        centre.add(x,y,z);
    }

    private void newBasementDraw(ArrayList<Vector3d> basementDetails,double c,double[][] list){
        Vector3d p,q,n=new Vector3d();
        if (!textureFlag) {
            p = basementDetails.get(1).sub(basementDetails.get(0), new Vector3d());
            q = basementDetails.get(2).sub(basementDetails.get(0), new Vector3d());
            n = p.cross(q);
        }
        for (int i=0;i<basementDetails.size();i+=15){
            glBegin(GL_TRIANGLES);

            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3dv(list[i]);
            glVertex3dv(list[i+1]);
            glVertex3dv(list[i+2]);
            glEnd();

            glBegin(GL_POLYGON);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3dv(list[i+3]);
            glVertex3dv(list[i+4]);
            glVertex3dv(list[i+5]);
            glVertex3dv(list[i+6]);
            glEnd();

            glBegin(GL_POLYGON);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3dv(list[i+7]);
            glVertex3dv(list[i+8]);
            glVertex3dv(list[i+9]);
            glVertex3dv(list[i+10]);
            glEnd();

            glBegin(GL_POLYGON);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3dv(list[i+11]);
            glVertex3dv(list[i+12]);
            glVertex3dv(list[i+13]);
            glVertex3dv(list[i+14]);
            glEnd();
        }
    }

    private void newBasementDraw(ArrayList<Vector3d> basementDetails,double c){
        Vector3d p,q,n=new Vector3d();
        if (!textureFlag) {
            p = basementDetails.get(1).sub(basementDetails.get(0), new Vector3d());
            q = basementDetails.get(2).sub(basementDetails.get(0), new Vector3d());
            n = p.cross(q);
        }
        for (int i=0;i<basementDetails.size();i+=15){
            glBegin(GL_TRIANGLES);

            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i).x,basementDetails.get(i).y,basementDetails.get(i).z);
            glVertex3d(basementDetails.get(i+1).x,basementDetails.get(i+1).y,basementDetails.get(i+1).z);
            glVertex3d(basementDetails.get(i+2).x,basementDetails.get(i+2).y,basementDetails.get(i+2).z);
            glEnd();

            glBegin(GL_POLYGON);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i+3).x,basementDetails.get(i+3).y,basementDetails.get(i+3).z);
            glVertex3d(basementDetails.get(i+4).x,basementDetails.get(i+4).y,basementDetails.get(i+4).z);
            glVertex3d(basementDetails.get(i+5).x,basementDetails.get(i+5).y,basementDetails.get(i+5).z);
            glVertex3d(basementDetails.get(i+6).x,basementDetails.get(i+6).y,basementDetails.get(i+6).z);
            glEnd();

            glBegin(GL_POLYGON);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i+7).x,basementDetails.get(i+7).y,basementDetails.get(i+7).z);
            glVertex3d(basementDetails.get(i+8).x,basementDetails.get(i+8).y,basementDetails.get(i+8).z);
            glVertex3d(basementDetails.get(i+9).x,basementDetails.get(i+9).y,basementDetails.get(i+9).z);
            glVertex3d(basementDetails.get(i+10).x,basementDetails.get(i+10).y,basementDetails.get(i+10).z);
            glEnd();

            glBegin(GL_POLYGON);
            glNormal3d(n.x*c,n.y*c,n.z*c);
            glVertex3d(basementDetails.get(i+11).x,basementDetails.get(i+11).y,basementDetails.get(i+11).z);
            glVertex3d(basementDetails.get(i+12).x,basementDetails.get(i+12).y,basementDetails.get(i+12).z);
            glVertex3d(basementDetails.get(i+13).x,basementDetails.get(i+13).y,basementDetails.get(i+13).z);
            glVertex3d(basementDetails.get(i+14).x,basementDetails.get(i+14).y,basementDetails.get(i+14).z);
            glEnd();
        }
    }

    private void basementDraw(){
        if (textureFlag){
            Texture.textureStorage.get(3).bind();
            if (animationFlag){
                simpleBasementDraw(basementDetailsLow,0);
                simpleBasementDraw(basementDetailsHigh,dist);
            }else{
                glCallList(id1);
                glCallList(id2);
            }

        }else {
            newBasementDraw(basementDetailsHigh,1);
            newBasementDraw(basementDetailsLow,-1);
        }
    }

    private void basementList(ArrayList<Vector3d> list,double bias){
        if (bias>0) id1=glGenLists(1);
        else id2=glGenLists(1);
        glNewList(bias>0 ? id1 : id2,GL_COMPILE);
        for (int i=0;i<list.size();i+=15){
            glBegin(GL_TRIANGLES);
            glVertex3d(list.get(i).x,list.get(i).y,list.get(i).z);
            glTexCoord2d(0.5,0.5);

            glVertex3d(list.get(i+12).x,list.get(i+12).y,list.get(i+12).z);
            glTexCoord2d(((list.get(i+12).x+bias)/a+1)/2,(list.get(i+12).y/b+1)/2);

            glVertex3d(list.get(i+13).x,list.get(i+13).y,list.get(i+13).z);
            glTexCoord2d(((list.get(i+13).x+bias)/a+1)/2,(list.get(i+13).y/b+1)/2);
            glEnd();
        }
        glEndList();
    }

    private void basementList(ArrayList<Vector3d> list,double bias,double[][] arr){
        if (bias>0) id1=glGenLists(1);
        else id2=glGenLists(1);
        glNewList(bias>0 ? id1 : id2,GL_COMPILE);
        for (int i=0;i<list.size();i+=15){
            glBegin(GL_TRIANGLES);
            glVertex3dv(arr[i]);
            glTexCoord2d(0.5,0.5);

            glVertex3dv(arr[i+12]);
            glTexCoord2d(((list.get(i+12).x+bias)/a+1)/2,(list.get(i+12).y/b+1)/2);

            glVertex3dv(arr[i+13]);
            glTexCoord2d(((list.get(i+13).x+bias)/a+1)/2,(list.get(i+13).y/b+1)/2);
            glEnd();
        }
        glEndList();
    }

    private void simpleBasementDraw(ArrayList<Vector3d> list,double bias,double[][] arr){
        if (animationFlag){
            for (int i=0;i<list.size();i+=15){
                glBegin(GL_TRIANGLES);
                glVertex3dv(arr[i]);
                glTexCoord2d(0.5,0.5);

                glVertex3dv(arr[i+12]);
                glTexCoord2d(((list.get(i+12).x+bias)/a+1)/2,(list.get(i+12).y/b+1)/2);

                glVertex3dv(arr[i+13]);
                glTexCoord2d(((list.get(i+13).x+bias)/a+1)/2,(list.get(i+13).y/b+1)/2);
                glEnd();
            }
        }else{
            glCallList(id1);
            glCallList(id2);
        }
    }

    private void simpleBasementDraw(ArrayList<Vector3d> list,double bias){
        if (animationFlag){
            for (int i=0;i<list.size();i+=15){
                glBegin(GL_TRIANGLES);
                glVertex3d(list.get(i).x,list.get(i).y,list.get(i).z);
                glTexCoord2d(0.5,0.5);

                glVertex3d(list.get(i+12).x,list.get(i+12).y,list.get(i+12).z);
                glTexCoord2d(((list.get(i+12).x+bias)/a+1)/2,(list.get(i+12).y/b+1)/2);

                glVertex3d(list.get(i+13).x,list.get(i+13).y,list.get(i+13).z);
                glTexCoord2d(((list.get(i+13).x+bias)/a+1)/2,(list.get(i+13).y/b+1)/2);
                glEnd();
            }
        }else{
            glCallList(id1);
            glCallList(id2);
        }
    }
}

