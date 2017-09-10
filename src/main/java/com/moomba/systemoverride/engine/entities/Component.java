package com.moomba.systemoverride.engine.entities;

import java.util.ArrayList;
import java.util.List;

public class Component {
    private List<Runnable> changeListeners;

    public Component(){
        changeListeners = new ArrayList<>();
    }

    protected void notifyListeners(){
        changeListeners.forEach(l -> l.run());
    }

    public void addChangeListener(Runnable runnable){
        changeListeners.add(runnable);
    }

    public void removeChangeListener(Runnable runnable){
        changeListeners.remove(runnable);
    }
}
