package com.moomba.systemoverride.game;

import com.moomba.systemoverride.engine.*;
import com.moomba.systemoverride.engine.entities.*;
import com.moomba.systemoverride.engine.entities.components.CameraComponent;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.OctreeComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.entities.systems.CameraMovementSystem;
import com.moomba.systemoverride.engine.entities.systems.OctreeDebugRenderSystem;
import com.moomba.systemoverride.engine.generation.*;
import com.moomba.systemoverride.engine.input.Key;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;

//TODO: Clean up the scene.
public class SOGame implements Scene{

    private Engine engine;
    private Function function = new SimplexNoiseFunction(124);
    private Mesh mesh;

    public static void main(String[] args){
        System.out.println("Starting System Override");
        Engine engine = new Engine();
        engine.start(new SOGame());
    }

    @Override
    public void init(Engine engine, AssetLoader loader) {
        this.engine = engine;

        addCamera(0, 2, 0);
        addTerrain(0, 0, 0, 7, 20f, 6f, 16884);

        engine.addSystem(new CameraMovementSystem());
        engine.setSystemState(OctreeDebugRenderSystem.class, false);

        engine.addUpdateable(new Runnable(){
            boolean wireframe = false;
            boolean registered = false;
            boolean octree = false;
            boolean registeredOctree = false;

            @Override
            public void run() {
                if(engine.getInputManager().isKeyPressed(Key.KEY_SPACE)){
                    if(!registered) {
                        if (wireframe) mesh.setPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                        else mesh.setPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                        wireframe = !wireframe;
                        registered = true;
                    }
                }else{
                    registered = false;
                }
                if(engine.getInputManager().isKeyPressed(Key.KEY_LETTER_T)){
                    if(!registeredOctree) {
                        if (octree) engine.setSystemState(OctreeDebugRenderSystem.class, false);
                        else engine.setSystemState(OctreeDebugRenderSystem.class, true);
                        octree = !octree;
                        registeredOctree = true;
                    }
                }else{
                    registeredOctree = false;
                }
            }
        });
    }

    private void addTerrain(float posX, float posY, float posZ, int detail, float size, float functionScale, int seed) {
        function.setScale(functionScale, functionScale, functionScale);

        Octree octree = new Octree(0, 0, 0, size);
        octree.forEachLeaf(Octree.Node::subdivide);
        octree.forEachLeaf(Octree.Node::subdivide);
        octree.forEachLeaf(Octree.Node::subdivide);
        octree.forEachLeaf(Octree.Node::subdivide);
        octree.forEachLeaf(Octree.Node::subdivide);

        HermiteDataGenerator hermiteDataGenerator = new HermiteDataGenerator();
        hermiteDataGenerator.generateHermiteDataFor(octree.getNode(), function);

        QEFMinimizer qefMinimizer = new QEFMinimizer();
        qefMinimizer.minimizeQEFsForTaggedNodes(octree.getNode(), function);

        DCSimplifier dcSimplifier = new DCSimplifier();
        dcSimplifier.simplify(octree.getNode(), 2);

        DCMesher dcMesher = new DCMesher();
        mesh = dcMesher.generateMesh(octree.getNode());

        Entity entity = new Entity();
        MeshComponent meshComponent = new MeshComponent(mesh);
        entity.addComponent(meshComponent);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(posX, posY, posZ);
        transformComponent.getScale().set(1);
        entity.addComponent(transformComponent);
        OctreeComponent octreeComponent = new OctreeComponent(octree);
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
