package com.moomba.systemoverride.engine.entities.systems;

import com.moomba.systemoverride.engine.*;
import com.moomba.systemoverride.engine.entities.Entity;
import com.moomba.systemoverride.engine.entities.EntitySystem;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.OctreeComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.input.InputManager;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

public class OctreeDebugRenderSystem implements EntitySystem {

    private static final Family family = new Family().includes(TransformComponent.class, OctreeComponent.class);
    private Mesh quadCube;
    private TransformComponent transformComponent;


    @Override
    public void init(AssetLoader loader) {
        quadCube = MeshBuilder.linecube(1, 1, 1, 1);
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
            Octree octree = entity.getComponent(OctreeComponent.class).getOctree();
            octree.processLeafs(node ->{
                transformComponent.reset();
                transformComponent.getPosition().set(node.center);
                transformComponent.getScale().set(node.edgeSize);
                renderer.queueMesh(quadCube, transformComponent.asTransformMatrix());
            });
        });
    }

    @Override
    public void dispose() {

    }
}
