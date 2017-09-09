package com.moomba.systemoverride.engine;

import java.util.List;

public interface EntitySystem {

    void initialize();

    Family getFamily();

    void process(List<Entity> entities);
}
