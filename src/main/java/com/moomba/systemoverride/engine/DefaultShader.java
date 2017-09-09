package com.moomba.systemoverride.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;

public class DefaultShader extends Shader{

    private int m_matrix;
    private int mvp_matrix;
    private int camera_location;

    public DefaultShader() {
        super(
                new File(DefaultShader.class.getClassLoader().getResource("defaultShader.vert").getPath()),
                new File(DefaultShader.class.getClassLoader().getResource("defaultShader.frag").getPath()));
    }

    @Override
    protected void getAllUniformLocations() {
        m_matrix = getUniformLocation("m_matrix");
        mvp_matrix = getUniformLocation("mvp_matrix");
        camera_location = getUniformLocation("camera_location");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "normal");
        bindAttribute(2, "color");
    }

    public void loadModelMatrix(Matrix4f modelMatrix){
        loadMatrix(m_matrix, modelMatrix);
    }

    public void loadModelViewProjectionMatrix(Matrix4f modelViewProjectionMatrix){
        loadMatrix(mvp_matrix, modelViewProjectionMatrix);
    }

    public void loadCameraPosition(Vector3f cameraPosition){
        load3DVector(camera_location, cameraPosition);
    }
}
