package com.moomba.systemoverride.engine.entities;

import com.moomba.systemoverride.engine.AssetLoader;
import com.moomba.systemoverride.engine.Family;
import com.moomba.systemoverride.engine.Renderer;
import com.moomba.systemoverride.engine.input.InputManager;

import java.util.List;

public interface EntitySystem {

    void init(AssetLoader loader);

    Family getFamily();

    void update(List<Entity> entities, InputManager inputManager);

    void render(List<Entity> entities, Renderer renderer);

    void dispose();
}
