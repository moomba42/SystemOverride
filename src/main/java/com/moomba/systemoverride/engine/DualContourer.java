package com.moomba.systemoverride.engine;

public class DualContourer {

    public Mesh contoure(Sampler sampler, float size){

        return null;
    }

    @FunctionalInterface
    public interface Sampler{
        float sample(float x, float y, float z);
    }
}
