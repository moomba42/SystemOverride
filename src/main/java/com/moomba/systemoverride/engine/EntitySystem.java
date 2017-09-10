package com.moomba.systemoverride.engine;

import java.util.List;

public interface EntitySystem {

    void init(AssetLoader loader);

    Family getFamily();

    void update(List<Entity> entities, InputManager inputManager);

    void render(List<Entity> entities, Renderer renderer);

    void dispose();
}
