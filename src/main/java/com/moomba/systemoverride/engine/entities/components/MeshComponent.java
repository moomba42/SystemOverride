package com.moomba.systemoverride.engine.entities.components;

import com.moomba.systemoverride.engine.Mesh;
import com.moomba.systemoverride.engine.entities.Component;

public class MeshComponent extends Component {
    private Mesh mesh;

    public MeshComponent(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
        notifyListeners();
    }
}
