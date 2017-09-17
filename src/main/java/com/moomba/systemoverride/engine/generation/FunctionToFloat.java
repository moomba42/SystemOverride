package com.moomba.systemoverride.engine.generation;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class FunctionToFloat {

    private Function function;

    public FunctionToFloat(Function function){
        this.function = function;
    }

    public float noise(float x, float y, float z) {
        return (float) function.noise(x, y, z);
    }

    public Vector3f normal(float x, float y, float z) {
        Vector3d normal = function.normal(x, y, z);
        return new Vector3f((float) normal.x, (float) normal.y, (float) normal.z);
    }

    public Function getSourceFunction(){
        return function;
    }
}
