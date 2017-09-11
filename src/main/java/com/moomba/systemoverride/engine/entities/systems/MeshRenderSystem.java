package com.moomba.systemoverride.engine.entities.systems;

import com.moomba.systemoverride.engine.AssetLoader;
import com.moomba.systemoverride.engine.Family;
import com.moomba.systemoverride.engine.Renderer;
import com.moomba.systemoverride.engine.entities.Entity;
import com.moomba.systemoverride.engine.entities.EntitySystem;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.input.InputManager;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.glPolygonMode;

public class MeshRenderSystem implements EntitySystem {

    private static final Family family = new Family().includes(MeshComponent.class, TransformComponent.class);

    @Override
    public void init(AssetLoader loader) {

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
        entities.forEach(entity -> renderer.queueMesh(
                entity.getComponent(MeshComponent.class).getMesh(),
                entity.getComponent(TransformComponent.class).asTransformMatrix()
        ));
    }

    @Override
    public void dispose() {

    }
}
