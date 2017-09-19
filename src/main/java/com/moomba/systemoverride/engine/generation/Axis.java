package com.moomba.systemoverride.engine.generation;

import org.joml.Vector3f;

public enum Axis {
    X(1, 0, 0),
    Y(0, 1, 0),
    Z(0, 0, 1);

    private final Vector3f direction;

    Axis(float x, float y, float z){
        direction = new Vector3f(x, y, z);
    }

    public Vector3f direction(){
        return direction;
    }

    public float x(){
        return direction.x;
    }
    public float y(){
        return direction.y;
    }
    public float z(){
        return direction.z;
    }
}
