package com.moomba.systemoverride.engine;

public interface ComponentListener {
    void componentAdded(Component component);
    void componentRemoved(Component component);
    void componentModified(Component component);
}
