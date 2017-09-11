package com.moomba.systemoverride.game;

import com.moomba.systemoverride.engine.*;
import com.moomba.systemoverride.engine.entities.*;
import com.moomba.systemoverride.engine.entities.components.CameraComponent;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.OctreeComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.entities.systems.CameraMovementSystem;

public class SOGame implements Scene{

    private Engine engine;

    public static void main(String[] args){
        System.out.println("Starting System Override");
        Engine engine = new Engine();
        engine.start(new SOGame());
    }

    @Override
    public void init(Engine engine, AssetLoader loader) {
        this.engine = engine;

        addAxes(0, 0, 0, 3);
        addCamera(2, 2, 2);
        addCube(0, 0, 0, 1, 1, 0, 1);
        addTerrain(0, 0, 0, 10, 16884);

        engine.addSystem(new CameraMovementSystem());
    }

    private void addTerrain(float posX, float posY, float posZ, float size, int seed) {
        SimplexNoiseGenerator generator = new SimplexNoiseGenerator(seed);
        DualContourer dualContourer = new DualContourer((x, y, z) -> (float) generator.noise(x, y, z));
        Octree octree = new Octree(size);
        octree.subdivide();
        octree.subdivide();
        octree.subdivide();
        Mesh mesh = dualContourer.contoure(octree);
        MeshComponent meshComponent = new MeshComponent(mesh);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(posX, posY, posZ);
        OctreeComponent octreeComponent = new OctreeComponent(octree);
        Entity entity = new Entity();
        //entity.addComponent(meshComponent);
        entity.addComponent(transformComponent);
        entity.addComponent(octreeComponent);
        engine.addEntity(entity);
    }

    private void addCube(float x, float y, float z, float size, float r, float g, float b) {
        MeshComponent meshComponent = new MeshComponent(MeshBuilder.cube(size, r, g, b));
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(x, y, z);
        Entity entity = new Entity();
        entity.addComponent(transformComponent);
        entity.addComponent(meshComponent);
        engine.addEntity(entity);
    }

    private void addCamera(float x, float y, float z) {
        TransformComponent transformComponentCam = new TransformComponent();
        transformComponentCam.getPosition().set(x, y, z);
        CameraComponent cameraComponent = new CameraComponent(true, 50, 1000, 600, 0.0001, 1000);
        Entity camera = new Entity();
        camera.addComponent(transformComponentCam);
        camera.addComponent(cameraComponent);
        engine.addEntity(camera);
    }

    private void addAxes(float x, float y, float z, float size) {
        MeshComponent meshComponent = new MeshComponent(MeshBuilder.axes(size));
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(x, y, z);
        Entity entity = new Entity();
        entity.addComponent(transformComponent);
        entity.addComponent(meshComponent);
        engine.addEntity(entity);
    }
}
