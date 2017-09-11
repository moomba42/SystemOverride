package com.moomba.systemoverride.engine.entities.components;

import com.moomba.systemoverride.engine.Octree;
import com.moomba.systemoverride.engine.entities.Component;

public class OctreeComponent extends Component{
    private Octree octree;

    public OctreeComponent(Octree octree) {
        this.octree = octree;
    }

    public Octree getOctree() {
        return octree;
    }
}
