package com.moomba.systemoverride.game;

import com.moomba.systemoverride.engine.*;

public class SOGame implements Scene{

    public static void main(String[] args){
        System.out.println("Starting System Override");
        Engine engine = new Engine();
        engine.start(new SOGame());
    }

    @Override
    public void init(Engine engine, AssetLoader loader) {
        float[] positions = {
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f,  1.0f, 0.0f,
                -1.0f,  1.0f, 0.0f,
        };

        float[] normals = {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };

        float[] colors = {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f
        };

        int[] indices = {
                0,1,2,
                0,2,3
        };

        Mesh testModel = new Mesh(positions, normals, colors, indices);
        testModel.uploadToGPU();

        MeshComponent meshComponent = new MeshComponent(testModel);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(0, 0,-2);
        Entity entity = new Entity();
        entity.addComponent(transformComponent);
        entity.addComponent(meshComponent);

        TransformComponent transformComponentCam = new TransformComponent();
        transformComponentCam.getPosition().z = 0;
        CameraComponent cameraComponent = new CameraComponent(true, 50, 1000, 600, 0.0001, 1000);
        Entity camera = new Entity();
        camera.addComponent(transformComponentCam);
        camera.addComponent(cameraComponent);

        engine.addEntity(entity);
        engine.addEntity(camera);


        engine.addSystem(new CameraMovementSystem());
    }
}
