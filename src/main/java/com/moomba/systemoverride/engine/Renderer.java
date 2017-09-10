package com.moomba.systemoverride.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Renderer implements Window.ResizeListener{

    private DefaultShader shader;
    private Entity entity;
    private Entity camera;

    //TODO: Remove (for testing purposes)
    private int width, height;

    public Renderer(int width, int height){
        this.width = width;
        this.height = height;
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
        Model3D testModel = new Model3D(positions, normals, colors);
        testModel.uploadToGPU();

        MeshComponent meshComponent = new MeshComponent(testModel);
        TransformComponent transformComponent = new TransformComponent();
        entity = new Entity();
        entity.addComponent(transformComponent);
        entity.addComponent(meshComponent);

        TransformComponent transformComponentCam = new TransformComponent();
        CameraComponent cameraComponent = new CameraComponent(true, 50, width, height, 0.0001, 1000);
        camera = new Entity();
        camera.addComponent(transformComponentCam);
        camera.addComponent(cameraComponent);
    }


    public void render(){

        entity.getComponent(TransformComponent.class).getRotation().rotateY(0.01f);
        camera.getComponent(TransformComponent.class).getPosition().z = -5;

        Matrix4f model = entity.getComponent(TransformComponent.class).asTransformMatrix();
        Matrix4f view = camera.getComponent(TransformComponent.class).asTransformMatrix();
        Matrix4f projection = camera.getComponent(CameraComponent.class).getProjectionMatrix();

        shader.use();
        shader.uploadMVPMatrix(model, view, projection);
        entity.getComponent(MeshComponent.class).getMesh().render();
    }

    public void dispose() {
        entity.getComponent(MeshComponent.class).getMesh().dispose();
    }

    //TODO: Remove (for testing purposes)
    @Override
    public void onResize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
