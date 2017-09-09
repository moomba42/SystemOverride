package com.moomba.systemoverride.engine;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private Shader shader;
    private Model3D testModel;

    public Renderer(){
    }

    public void init(){

        float[] positions = {
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                0.0f,  1.0f, 0.0f,
        };


        try {
            shader = new DefaultShader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        testModel = new Model3D(positions);
        testModel.uploadToGPU();
    }


    public void render(){
        shader.use();
        testModel.render();
    }

    public void dispose() {
        testModel.dispose();
    }
}
