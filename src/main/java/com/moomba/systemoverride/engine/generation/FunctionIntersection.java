package com.moomba.systemoverride.engine.generation;

import org.joml.Vector3f;

public class FunctionIntersection {

    private final Vector3f position;
    private final Vector3f normal;

    public FunctionIntersection(Vector3f position, Vector3f normal) {
        this.position = position;
        this.normal = normal;
    }

    public FunctionIntersection(){
        this.position = new Vector3f();
        this.normal = new Vector3f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getNormal() {
        return normal;
    }
}
