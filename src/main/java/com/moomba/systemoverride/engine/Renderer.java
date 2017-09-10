package com.moomba.systemoverride.engine;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer{

    private DefaultShader shader;
    Matrix4f viewMatrix;
    Matrix4f projectionMatrix;
    private Map<Mesh, List<Matrix4f>> meshesToRender;

    public Renderer(){
        viewMatrix = new Matrix4f().identity();
        projectionMatrix = new Matrix4f().identity();
        meshesToRender = new HashMap<>();
    }

    public void init(){
        try {
            shader = new DefaultShader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queueMesh(Mesh mesh, Matrix4f transform){
        if(!meshesToRender.containsKey(mesh))
            meshesToRender.put(mesh, new ArrayList<>());
        meshesToRender.get(mesh).add(transform);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix){
        this.projectionMatrix = projectionMatrix;
        shader.use();
        shader.uploadProjectionMatrix(projectionMatrix);
    }

    public void loadViewMatrix(Matrix4f viewMatrix){
        this.viewMatrix = viewMatrix;
        shader.use();
        shader.uploadViewMatrix(viewMatrix);
    }

    public void render(){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        shader.use();
        meshesToRender.forEach((mesh, transforms)->{
            mesh.bind();
            transforms.forEach(transform ->{
                shader.uploadModelMatrix(transform);
                mesh.render();
            });
            mesh.unbind();
        });
        meshesToRender.clear();
    }

    public void dispose() {
        shader.dispose();
    }
}
