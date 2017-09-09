package com.moomba.systemoverride.engine;

public interface EntityListener {
    void entityAdded(Entity entity);
    void entityRemoved(Entity entity);
    void entityModified(Entity entity);
}
