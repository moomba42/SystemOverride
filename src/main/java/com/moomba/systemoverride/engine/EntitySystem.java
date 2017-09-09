package com.moomba.systemoverride.engine;

import java.util.List;

public interface EntitySystem {

    void init();

    Family getFamily();

    void process(List<Entity> entities);

    void dispose();
}
