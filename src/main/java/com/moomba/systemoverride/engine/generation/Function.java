package com.moomba.systemoverride.engine.generation;

import org.joml.Vector3d;

public interface Function {
    double noise(double x, double y, double z);
    Vector3d normal(double x, double y, double z);
}
