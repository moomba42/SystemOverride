package com.moomba.systemoverride.engine.entities.components;

import com.moomba.systemoverride.engine.entities.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent extends Component {
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;

    public TransformComponent(){
        reset();
    }

    public void reset() {
        position = new Vector3f(0, 0, 0);
        rotation = new Quaternionf();
        scale = new Vector3f(1);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Matrix4f asTransformMatrix(){
        Matrix4f transformMatrix = new Matrix4f()
                .identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
        return transformMatrix;
    }
}
