package com.moomba.systemoverride.engine;

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

        float[] normals = {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
        };

        float[] colors = {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
        };


        try {
            shader = new DefaultShader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        testModel = new Model3D(positions, normals, colors);
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
