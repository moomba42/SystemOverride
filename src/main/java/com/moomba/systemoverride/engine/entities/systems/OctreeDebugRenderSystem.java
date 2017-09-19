package com.moomba.systemoverride.engine.entities.systems;

import com.moomba.systemoverride.engine.*;
import com.moomba.systemoverride.engine.entities.Entity;
import com.moomba.systemoverride.engine.entities.EntitySystem;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.OctreeComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.generation.Octree;
import com.moomba.systemoverride.engine.input.InputManager;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

public class OctreeDebugRenderSystem implements EntitySystem {

    private static final Family family = new Family().includes(TransformComponent.class, OctreeComponent.class);
    private Mesh quadCube;
    private Mesh vertexCube;
    private TransformComponent transformComponent;


    @Override
    public void init(AssetLoader loader) {
        quadCube = MeshBuilder.linecube(1, 1, 1, 1);
        vertexCube = MeshBuilder.cube(0.05f, 0, 0, 1);
        transformComponent = new TransformComponent();
    }

    @Override
    public Family getFamily() {
        return family;
    }

    @Override
    public void update(List<Entity> entities, InputManager inputManager) {

    }

    @Override
    public void render(List<Entity> entities, Renderer renderer) {
        entities.forEach(entity -> {
            TransformComponent entityTransformComponent = entity.getComponent(TransformComponent.class);
            Octree octree = entity.getComponent(OctreeComponent.class).getOctree();
            octree.forEachLeaf(node ->{
                if(!node.isTagged()) return;
                transformComponent.reset();
                transformComponent.getPosition().set(node.getCenter()).add(entityTransformComponent.getPosition());
                transformComponent.getScale().set(node.getEdgeSize());
                renderer.queueMesh(quadCube, transformComponent.asTransformMatrix());
                transformComponent.reset();
                transformComponent.getPosition().set(node.getQEFMinimizer().getPosition()).add(entityTransformComponent.getPosition());
                transformComponent.getScale().set(1);
                renderer.queueMesh(vertexCube, transformComponent.asTransformMatrix());
            });
        });
    }

    @Override
    public void dispose() {

    }
}
