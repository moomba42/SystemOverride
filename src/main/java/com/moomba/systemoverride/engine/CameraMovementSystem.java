package com.moomba.systemoverride.engine;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class CameraMovementSystem implements EntitySystem{

    private static final Family family = new Family().includes(CameraComponent.class, TransformComponent.class);

    private double oldMouseX = 0;
    private double oldMouseY = 0;

    @Override
    public void init(AssetLoader loader) {

    }

    @Override
    public Family getFamily() {
        return family;
    }

    @Override
    public void update(List<Entity> entities, InputManager inputManager) {
        float speed = 0.1f;
        float lookSpeed = 0.001f;
        float mDX = (float) -((inputManager.getMouseX() - oldMouseX))*lookSpeed;
        float mDY = (float) -((inputManager.getMouseY() - oldMouseY))*lookSpeed;
        oldMouseX = inputManager.getMouseX();
        oldMouseY = inputManager.getMouseY();

        entities.forEach(camera -> {
            TransformComponent transform = camera.getComponent(TransformComponent.class);


            //Movement
            Vector3f forward = new Vector3f(0, 0, -1);
            transform.getRotation().transform(forward);

            Vector3f right = new Vector3f(1, 0, 0);
            transform.getRotation().transform(right);

            Vector3f up = new Vector3f(0, 1, 0);
            transform.getRotation().transform(up);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_W))
                transform.getPosition().add(forward.x*speed, forward.y*speed, forward.z*speed);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_A))
                transform.getPosition().add(-right.x*speed, -right.y*speed, -right.z*speed);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_S))
                transform.getPosition().add(-forward.x*speed, -forward.y*speed, -forward.z*speed);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_D))
                transform.getPosition().add(right.x*speed, right.y*speed, right.z*speed);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_Z))
                transform.getPosition().add(-up.x*speed, -up.y*speed, -up.z*speed);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_X))
                transform.getPosition().add(up.x*speed, up.y*speed, up.z*speed);


            //Rotation
            transform.getRotation().rotateX(mDY);
            transform.getRotation().rotateY(mDX);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_Q))
                transform.getRotation().rotateZ(0.05f);

            if(inputManager.isKeyPressed(Key.KEY_LETTER_E))
                transform.getRotation().rotateZ(-0.05f);
        });
    }

    @Override
    public void render(List<Entity> entities, Renderer renderer) {

    }

    @Override
    public void dispose() {

    }
}
