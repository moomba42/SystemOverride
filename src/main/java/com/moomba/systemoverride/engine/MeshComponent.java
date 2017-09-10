package com.moomba.systemoverride.engine;

public class MeshComponent extends Component{
    private Model3D mesh;

    public MeshComponent(Model3D mesh) {
        this.mesh = mesh;
    }

    public Model3D getMesh() {
        return mesh;
    }

    public void setMesh(Model3D mesh) {
        this.mesh = mesh;
        notifyListeners();
    }
}
