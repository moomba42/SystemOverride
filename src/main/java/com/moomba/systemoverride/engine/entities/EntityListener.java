package com.moomba.systemoverride.engine.entities;

public interface EntityListener {
    void entityAdded(Entity entity);
    void entityRemoved(Entity entity);
    void entityModified(Entity entity);
}
