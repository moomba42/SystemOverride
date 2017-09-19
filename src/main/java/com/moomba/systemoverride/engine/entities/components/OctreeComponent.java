package com.moomba.systemoverride.engine.entities.components;

import com.moomba.systemoverride.engine.entities.Component;
import com.moomba.systemoverride.engine.generation.Octree;

public class OctreeComponent extends Component{
    private Octree octree;

    public OctreeComponent(Octree octree) {
        this.octree = octree;
    }

    public Octree getOctree() {
        return octree;
    }
}
