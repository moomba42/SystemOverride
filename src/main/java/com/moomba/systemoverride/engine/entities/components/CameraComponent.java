package com.moomba.systemoverride.engine.entities.components;

import com.moomba.systemoverride.engine.entities.Component;
import org.joml.Matrix4f;

public class CameraComponent extends Component {
    private boolean active;

    private double fieldOfViewDeg;// in degrees
    private double nearPlane;
    private double farPlane;
    private double screenWidth;
    private double screenHeight;

    private Matrix4f projectionMatrix;

    public CameraComponent(boolean active, double fieldOfViewDeg, double screenWidth, double screenHeight, double nearPlane, double farPlane) {
        this.active = active;
        this.fieldOfViewDeg = fieldOfViewDeg;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        calculateProjectionMatrix();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg = fieldOfViewDeg;
        calculateProjectionMatrix();
        notifyListeners();
    }

    public void setNearPlane(double nearPlane) {
        this.nearPlane = nearPlane;
        calculateProjectionMatrix();
        notifyListeners();
    }

    public void setFarPlane(double farPlane) {
        this.farPlane = farPlane;
        calculateProjectionMatrix();
        notifyListeners();
    }

    public void setScreenWidth(double screenWidth) {
        this.screenWidth = screenWidth;
        calculateProjectionMatrix();
        notifyListeners();
    }

    public void setScreenHeight(double screenHeight) {
        this.screenHeight = screenHeight;
        calculateProjectionMatrix();
        notifyListeners();
    }

    public boolean isActive() {
        return active;
    }

    public double getFieldOfViewDeg() {
        return fieldOfViewDeg;
    }

    public double getNearPlane() {
        return nearPlane;
    }

    public double getFarPlane() {
        return farPlane;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }

    private void calculateProjectionMatrix(){
        double aspectRatio = screenWidth / screenHeight;
        double yScale = cotangent(Math.toRadians(fieldOfViewDeg / 2.0));
        double xScale = yScale / aspectRatio;
        double frustrumLength = farPlane - nearPlane;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00((float) xScale);
        projectionMatrix.m11((float) yScale);
        projectionMatrix.m22((float) -((farPlane + nearPlane) / frustrumLength));
        projectionMatrix.m23((float) -1);
        projectionMatrix.m32((float) -((2 * nearPlane * farPlane) / frustrumLength));
        projectionMatrix.m33((float) 0);

    }

    private double cotangent(double angle){
        return 1.0/Math.tan(angle);
    }
}
