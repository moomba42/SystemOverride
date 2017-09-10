package com.moomba.systemoverride.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TransformComponent extends Component{
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public TransformComponent(){
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        notifyListeners();
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        notifyListeners();
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
        notifyListeners();
    }

    public Matrix4f asTransformMatrix(){
        Matrix4f transformMatrix = new Matrix4f()
                .identity()
                .translate(position.x, position.y, position.z)
                .rotateX(-rotation.x)
                .rotateY(-rotation.y)
                .rotateZ(-rotation.z)
                .scale(scale.x, scale.y, scale.z);
        return transformMatrix;
    }
}
