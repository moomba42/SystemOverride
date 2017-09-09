package com.moomba.systemoverride.engine;

public class Renderer {

    private Shader shader;

    public Renderer(){

    }

    public void init(){
        shader = new DefaultShader();
    }


    public void render(){
        shader.bind();

        shader.unbind();
    }

    public void dispose() {

    }
}
