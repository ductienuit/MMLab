package vn.edu.uit.mmlab.modal;

import java.util.ArrayList;

public class FaceResult {
    public ArrayList<FaceInfor> getResult() {
        return result;
    }

    public void setResult(ArrayList<FaceInfor> result) {
        this.result = result;
    }

    public FaceResult(ArrayList<FaceInfor> result) {
        this.result = result;
    }

    ArrayList<FaceInfor> result;
    public class FaceInfor{
        String name;

        public FaceInfor(String name, String h, String w, String y, String x, String probs) {
            this.name = name;
            this.h = h;
            this.w = w;
            this.y = y;
            this.x = x;
            this.probs = probs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getH() {
            return h;
        }

        public void setH(String h) {
            this.h = h;
        }

        public String getW() {
            return w;
        }

        public void setW(String w) {
            this.w = w;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getProbs() {
            return probs;
        }

        public void setProbs(String probs) {
            this.probs = probs;
        }

        String h;
        String w;
        String y;
        String x;
        String probs;
    }
}


