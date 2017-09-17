package com.moomba.systemoverride.engine.generation;

import org.joml.Vector3d;

public class SphereFunction implements Function {

    private Vector3d pos;
    private Vector3d scale;
    private double radius;

    public SphereFunction(double posX, double posY, double posZ, double scaleX, double scaleY, double scaleZ, double radius){
        this.pos = new Vector3d(posX, posY, posZ);
        this.scale = new Vector3d(scaleX, scaleY, scaleZ);
        this.radius = radius;
    }

    @Override
    public double noise(double x, double y, double z) {
        return ( ((x*scale.x)-pos.x) * ((x*scale.x)-pos.x) )+
               ( ((y*scale.y)-pos.y) * ((y*scale.y)-pos.y) )+
               ( ((z*scale.z)-pos.z) * ((z*scale.z)-pos.z) )+
               -(radius*radius);
    }

    @Override
    public Vector3d normal(double x, double y, double z) {
        return new Vector3d((x*scale.x)-pos.x, (y*scale.y)-pos.y, (z*scale.z)-pos.z).normalize();
    }
}
