package com.moomba.systemoverride.engine;

public class DualContourer {

    private Sampler sampler;

    public DualContourer(Sampler sampler){
        this.sampler = sampler;
    }

    public Mesh contoure(Octree octree){


        return null;
    }

    @FunctionalInterface
    public interface Sampler{
        float sample(float x, float y, float z);
    }
}
