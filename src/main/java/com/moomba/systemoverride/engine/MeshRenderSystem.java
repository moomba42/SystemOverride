package com.moomba.systemoverride.engine;

import java.util.List;

public class MeshRenderSystem implements EntitySystem{

    private static final Family family = new Family().includes(MeshComponent.class, TransformComponent.class);;

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
