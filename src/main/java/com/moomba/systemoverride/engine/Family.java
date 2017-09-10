package com.moomba.systemoverride.engine;

import java.util.ArrayList;
import java.util.List;

public class Family {
    private List<Class<? extends Component>> excludes;
    private List<Class<? extends Component>> includes;
    private List<Class<? extends Component>> includesOneOf;

    public Family(){
        excludes = new ArrayList<>();
        includes = new ArrayList<>();
        includesOneOf = new ArrayList<>();
    }

    public Family includes(Class<? extends Component>... components){
        includes.clear();
        for (Class<? extends Component> component : components)
            includes.add(component);
        return this;
    }

    public Family excludes(Class<? extends Component>... components){
        excludes.clear();
        for (Class<? extends Component> component : components)
            excludes.add(component);
        return this;

    }

    public Family includesOneOf(Class<? extends Component>... components){
        includesOneOf.clear();
        for (Class<? extends Component> component : components)
            includesOneOf.add(component);
        return this;

    }

    public List<Entity> filter(List<Entity> entities){
        List<Entity> filteredList = new ArrayList<>();
        entities.forEach(entity -> {if(qualifies(entity)) filteredList.add(entity);});
        return filteredList;
    }

    public boolean qualifies(Entity entity){
        for (Class<? extends Component> exclude : excludes) {
            if(entity.hasComponent(exclude)) return false;
        }
        for (Class<? extends Component> include : includes) {
            if(!entity.hasComponent(include)) return false;
        }
        if(includesOneOf.size() <= 0) {
            return true;
        } else {
            for (Class<? extends Component> oneOf : includesOneOf) {
                if (entity.hasComponent(oneOf)) return true;
            }
        }
        return false;
    }
}
