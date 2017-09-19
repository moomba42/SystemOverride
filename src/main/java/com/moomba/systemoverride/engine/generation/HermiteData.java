package com.moomba.systemoverride.engine.generation;

import java.util.ArrayList;
import java.util.List;

public class HermiteData {

    private final List<FunctionIntersection> functionIntersections;
    private float[] cornerSigns;
    private float[] cornerQEFs;
    private float centerQEF;
    private int group;

    public HermiteData(){
        this.cornerQEFs = new float[8];
        this.cornerSigns = new float[8];
        this.functionIntersections = new ArrayList<>();
    }

    public List<FunctionIntersection> getFunctionIntersections() {
        return functionIntersections;
    }

    public float getCornerQEF(int corner){
        return cornerQEFs[corner];
    }

    public void setCornerQEF(int corner, float qef){
        cornerQEFs[corner] = qef;
    }

    public float getCornerSign(int corner){
        return cornerSigns[corner];
    }

    public void setCornerSign(int corner, float sign){
        cornerSigns[corner] = sign;
    }

    public float getCenterQEF() {
        return centerQEF;
    }

    public void setCenterQEF(float centerQEF) {
        this.centerQEF = centerQEF;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

}
