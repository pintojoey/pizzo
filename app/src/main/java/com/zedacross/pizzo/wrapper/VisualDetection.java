package com.zedacross.pizzo.wrapper;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joey on 9/23/2016.
 */

public class VisualDetection {


    private ArrayList<Detection_class> detection_classes;
    public ArrayList<Detection_class> getDetection_classes() {
        return detection_classes;
    }

    public void setDetection_classes(ArrayList<Detection_class> detection_classes) {
        this.detection_classes = detection_classes;
    }
    public VisualDetection(String response) throws JSONException {
        this.detection_classes=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(response);
        System.out.print(jsonObject.toString());
        System.out.print(jsonObject.getJSONArray("images").getJSONObject(0).getJSONArray("classifiers").toString());
        JSONArray classes=jsonObject.getJSONArray("images").getJSONObject(0).getJSONArray("classifiers").getJSONObject(0).getJSONArray("classes");

        for(int i=0;i<classes.length();i++){
            JSONObject j_obj=classes.getJSONObject(i);
            Detection_class detection = new Detection_class();
            detection.score=j_obj.getDouble("score");
            detection.class_name=j_obj.getString("class");
            this.detection_classes.add(detection);
        }

    }
public    class Detection_class{
      private String class_name="";
        private double score=0.0;

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

}
