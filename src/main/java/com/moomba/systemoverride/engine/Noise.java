package com.moomba.systemoverride.engine;

import org.joml.Vector3d;
import org.joml.Vector3f;

public interface Noise {
    double noise(double x, double y, double z);
    float noisef(float x, float y, float z);
    Vector3d gradient(double x, double y, double z);
    Vector3f gradientf(float x, float y, float z);
}
