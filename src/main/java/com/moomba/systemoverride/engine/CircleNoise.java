package com.moomba.systemoverride.engine;

import org.joml.Vector3d;
import org.joml.Vector3f;


public class CircleNoise implements Noise {

    private double px;
    private double py;
    private double pz;
    private double radius;

    public CircleNoise(double x, double y, double z, double radius){
        this.px = x;
        this.py = y;
        this.pz = z;
        this.radius = radius;
    }

    @Override
    public double noise(double x, double y, double z) {
        return ((x-px)*(x-px))+((y-py)*(y-py))+((z-pz)*(z-pz))-(radius*radius);
    }

    @Override
    public float noisef(float x, float y, float z) {
        return (float) noise(x, y, z);
    }

    @Override
    public Vector3d gradient(double x, double y, double z) {
        return new Vector3d(x-px, y-py, z-pz).normalize();
    }

    @Override
    public Vector3f gradientf(float x, float y, float z) {
        Vector3d gradient = gradient(x, y, z);
        return new Vector3f((float) gradient.x, (float) gradient.y, (float) gradient.z);
    }
}
