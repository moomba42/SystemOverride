package com.moomba.systemoverride.engine;

import java.util.List;

public class CameraMovementSystem implements EntitySystem{

    private static final Family family = new Family().includes(CameraComponent.class, TransformComponent.class);

    private double oldMouseX = 0;
    private double oldMouseY = 0;
    private double vX = 0;
    private double vY = 0;

    @Override
    public void init(AssetLoader loader) {

    }

    @Override
    public Family getFamily() {
        return family;
    }

    @Override
    public void update(List<Entity> entities, InputManager inputManager) {
        vX = (inputManager.getMouseX() - oldMouseX) * 0.01;
        vY = (inputManager.getMouseY() - oldMouseY) * 0.01;
        oldMouseX = inputManager.getMouseX();
        oldMouseY = inputManager.getMouseY();

        entities.forEach(camera -> {
            TransformComponent transform = camera.getComponent(TransformComponent.class);
            transform.getRotation().rotateY((float) vX);
            transform.getRotation().rotateLocalX((float) vY);
        });
    }

    @Override
    public void render(List<Entity> entities, Renderer renderer) {

    }

    @Override
    public void dispose() {

    }
}
