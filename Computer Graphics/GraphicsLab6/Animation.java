import org.joml.Vector3d;
import org.json.simple.JSONObject;

public class Animation {
    private Cylinder cylinder;
    private double t=0,delta=0.01;
    Animation(Cylinder c){
        cylinder=c;
    }

    JSONObject toJSON(){
        JSONObject object=new JSONObject();
        object.put("t",t);
        object.put("delta",delta);
        JSONObject result=new JSONObject();
        result.put("Animation",object);
        return result;
    }

    void load(JSONObject dataToLoad){
        t=(double)dataToLoad.get("t");
        delta=(double)dataToLoad.get("delta");
    }

    void setDelta(double d){
        if (d>0 && d<1) delta=d;
    }

    void animate() throws InterruptedException {
        if (t < 0) {
            t = 0;
            move();
            delta = -delta;
            cylinder.changeAnimationFlag();
        }else if (t > 1) {
            delta = -delta;
            t = 1;
            move();
        }else{
            t += delta;
            move();
        }

    }

    private void move() {
        for (int i=0;i<cylinder.basementHigh.size();i++){
            cylinder.basementHigh.get(i).set(calc(cylinder.basementHighC.get(i),
                    new Vector3d(cylinder.basementHighC.get(i).x,
                            2*Math.abs(cylinder.basementHighC.get(i).x),
                            cylinder.basementHighC.get(i).z),
                    new Vector3d(-cylinder.basementHighC.get(i).x,
                            2*Math.abs(cylinder.basementHighC.get(i).x),
                            cylinder.basementHighC.get(i).z),
                    new Vector3d(-cylinder.basementHighC.get(i).x,
                            cylinder.basementHighC.get(i).y,
                            cylinder.basementHighC.get(i).z)));
        }

        for (int i=0;i<cylinder.basementDetailsHigh.size();i++){
            cylinder.basementDetailsHigh.get(i).set(calc(cylinder.basementDetailsHighC.get(i),
                    new Vector3d(cylinder.basementDetailsHighC.get(i).x,
                            2*Math.abs(cylinder.basementDetailsHighC.get(i).x),
                            cylinder.basementDetailsHighC.get(i).z),
                    new Vector3d(-cylinder.basementDetailsHighC.get(i).x,
                            2*Math.abs(cylinder.basementDetailsHighC.get(i).x),
                            cylinder.basementDetailsHighC.get(i).z),
                    new Vector3d(-cylinder.basementDetailsHighC.get(i).x,
                            cylinder.basementDetailsHighC.get(i).y,
                            cylinder.basementDetailsHighC.get(i).z)));
        }

        for (int i=0;i<cylinder.basementLow.size();i++){
            cylinder.basementLow.get(i).set(calc(cylinder.basementLowC.get(i),
                    new Vector3d(cylinder.basementLowC.get(i).x,
                            2*Math.abs(cylinder.basementLowC.get(i).x),
                            cylinder.basementLowC.get(i).z),
                    new Vector3d(-cylinder.basementLowC.get(i).x,
                            2*Math.abs(cylinder.basementLowC.get(i).x),
                            cylinder.basementLowC.get(i).z),
                    new Vector3d(-cylinder.basementLowC.get(i).x,
                            cylinder.basementLowC.get(i).y,
                            cylinder.basementLowC.get(i).z)));
        }

        for (int i=0;i<cylinder.basementDetailsLow.size();i++){
            cylinder.basementDetailsLow.get(i).set(calc(cylinder.basementDetailsLowC.get(i),
                    new Vector3d(cylinder.basementDetailsLowC.get(i).x,
                            2*Math.abs(cylinder.basementDetailsLowC.get(i).x),
                            cylinder.basementDetailsLowC.get(i).z),
                    new Vector3d(-cylinder.basementDetailsLowC.get(i).x,
                            2*Math.abs(cylinder.basementDetailsLowC.get(i).x),
                            cylinder.basementDetailsLowC.get(i).z),
                    new Vector3d(-cylinder.basementDetailsLowC.get(i).x,
                            cylinder.basementDetailsLowC.get(i).y,
                            cylinder.basementDetailsLowC.get(i).z)));
        }


        for (int j=0;j<cylinder.layers.size();j++){
            for (int i=0;i<cylinder.layers.get(j).size();i++){
                cylinder.layers.get(j).get(i).set(calc(cylinder.layersC.get(j).get(i),
                        new Vector3d(cylinder.layersC.get(j).get(i).x,
                                2*Math.abs(cylinder.layersC.get(j).get(i).x),
                                cylinder.layersC.get(j).get(i).z),
                        new Vector3d(-cylinder.layersC.get(j).get(i).x,
                                2*Math.abs(cylinder.layersC.get(j).get(i).x),
                                cylinder.layersC.get(j).get(i).z),
                        new Vector3d(-cylinder.layersC.get(j).get(i).x,
                                cylinder.layersC.get(j).get(i).y,
                                cylinder.layersC.get(j).get(i).z)));
            }
        }
    }

    private Vector3d calc(Vector3d p1,Vector3d p2,Vector3d p3,Vector3d p4){
        double v=1-t;
        return p1.mul(v*v*v,new Vector3d()).add(p2.mul(3*v*v*t,new Vector3d()))
                .add(p3.mul(3*v*t*t,new Vector3d())).add(p4.mul(t*t*t,new Vector3d()));
    }

}
