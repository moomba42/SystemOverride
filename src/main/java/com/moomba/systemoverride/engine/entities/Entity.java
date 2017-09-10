package com.moomba.systemoverride.engine.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    private Map<Class<? extends Component>, Component> components;
    private List<ComponentListener> listeners;

    public Entity(){
        components = new HashMap<>();
        listeners = new ArrayList<ComponentListener>();
    }

    public void addComponent(Component component){
        //notify all component listeners that this component changed if it emits a change signal
        component.addChangeListener(() -> listeners.forEach(l -> l.componentModified(component)));

        //add the component to the map
        components.put(component.getClass(), component);

        //emit the componentAdded signal to all listeners
        listeners.forEach(l -> l.componentAdded(component));
    }

    public <T extends Component> T getComponent(Class<T> claz){
        return (T) components.get(claz);
    }

    public <T extends Component> boolean hasComponent(Class<T> claz){
        return components.containsKey(claz);
    }

    public <T extends Component> void removeComponent(Class<T> claz){
        //don't trigger signals if the component isn't even there, return
        if(!hasComponent(claz)) return;

        //delete the component and stire the deleted component in a variable
        Component deletedComponent = components.remove(claz);

        //notify the listeners of the deletion
        listeners.forEach(l -> l.componentRemoved(deletedComponent));
    }
}
