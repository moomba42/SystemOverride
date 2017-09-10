package com.moomba.systemoverride.engine;

import org.joml.Matrix4f;

import java.io.File;

public class DefaultShader extends Shader{

    private int model_matrix_location = -1;
    private int view_matrix_location = -1;
    private int projection_matrix_location = -1;

    public DefaultShader() throws Exception {
        super(new File(DefaultShader.class.getClassLoader().getResource("defaultShader.vert").getPath()),
              new File(DefaultShader.class.getClassLoader().getResource("defaultShader.frag").getPath()));

        model_matrix_location = getUniformLocation("model_matrix");
        view_matrix_location = getUniformLocation("view_matrix");
        projection_matrix_location = getUniformLocation("projection_matrix");
    }

    public void uploadModelMatrix(Matrix4f model){
        loadMatrix(model_matrix_location, model);
    }
    public void uploadViewMatrix(Matrix4f view){
        loadMatrix(view_matrix_location, view);
    }

    public void uploadProjectionMatrix(Matrix4f projection){
        loadMatrix(projection_matrix_location, projection);
    }
}
