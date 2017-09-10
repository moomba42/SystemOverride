package com.moomba.systemoverride.engine.entities.systems;

import com.moomba.systemoverride.engine.AssetLoader;
import com.moomba.systemoverride.engine.Family;
import com.moomba.systemoverride.engine.Renderer;
import com.moomba.systemoverride.engine.entities.Entity;
import com.moomba.systemoverride.engine.entities.EntitySystem;
import com.moomba.systemoverride.engine.entities.components.CameraComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.input.InputManager;

import java.util.List;

public class CameraSystem implements EntitySystem {

    private static final Family family = new Family().includes(CameraComponent.class, TransformComponent.class);
    private Entity previousCamera = null;

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
        entities.forEach(camera -> {
            if(camera.getComponent(CameraComponent.class).isActive()){
                renderer.loadViewMatrix(camera.getComponent(TransformComponent.class).asTransformMatrix().invert());
                if(camera != previousCamera) {
                    renderer.loadProjectionMatrix(camera.getComponent(CameraComponent.class).getProjectionMatrix());
                    previousCamera = camera;
                }
            }
        });
    }

    @Override
    public void dispose() {

    }
}
