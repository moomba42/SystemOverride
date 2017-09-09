package com.moomba.systemoverride.engine;

import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.HashMap;
import java.util.Map;

public class InputManager {

    //callbacks
    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseCallback;
    private GLFWCursorPosCallback posCallback;
    private GLFWScrollCallback scrollCallback;
    private GLFWCursorEnterCallback enterCallback;

    //window listening on
    private Window window;

    //state
    private Vector2d mousePosition;
    private Map<Key, Boolean> keyStates;

    public InputManager(Window window){
        this.window = window;
        mousePosition = new Vector2d();
        keyStates = new HashMap<>();
        for(Key key : Key.values()){
            keyStates.put(key, false);
        }
    }

    //function for setting up callbacks
    public void init() {
        glfwSetMouseButtonCallback(InputManager.this.window.getID(), mouseCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if(window == InputManager.this.window.getID()){
                    switch(action) {
                        case GLFW.GLFW_RELEASE:
                            mouseButtonAction(button, false);
                            break;
                        case GLFW.GLFW_PRESS:
                            mouseButtonAction(button, true);
                            break;
                        default:
                    }
                }
            }
        });

        glfwSetCursorPosCallback(InputManager.this.window.getID(), posCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if(window == InputManager.this.window.getID()){
                    mousePosChanged(xpos, ypos);
                }
            }
        });

        glfwSetScrollCallback(InputManager.this.window.getID(), scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if(window == InputManager.this.window.getID()){
                    mouseScrolled(xoffset, yoffset);
                }
            }
        });

        glfwSetCursorEnterCallback(InputManager.this.window.getID(), enterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                if(window == InputManager.this.window.getID()){
                    mouseEntered(entered);
                }
            }
        });

        //set the key callback to a callback that notifies the listeners assigned to this window
        glfwSetKeyCallback(InputManager.this.window.getID(), (keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int keycode, int scancode, int action, int mods) {
                if(window == InputManager.this.window.getID()){
                    Key key = Key.getKeyForGLWFKeycode(keycode, Key.KEY_UNKNOWN);
                    switch(action) {
                        case GLFW.GLFW_RELEASE:
                            key(key, false);
                            break;
                        case GLFW.GLFW_PRESS:
                            key(key, true);
                            break;
                        default:
                    }
                }
            }
        }));
    }


    //public functions for polling
    public double getMouseX(){
        return mousePosition.x;
    }

    public double getMouseY(){
        return mousePosition.y;
    }

    public boolean isKeyPressed(Key key){
        return keyStates.get(key);
    }


    //--[destination functions for callbacks]--//

    //glfwSetKeyCallback
    private void key(Key key, boolean pressed){
        keyStates.replace(key, pressed);
    }

    //glfwSetCursorPosCallback
    private void mousePosChanged(double newX, double newY){
        mousePosition.set(newX, newY);
    }

    //glfwSetScrollCallback
    private void mouseScrolled(double amountX, double amountY){

    }

    //glfwSetMouseButtonCallback
    private void mouseButtonAction(int button, boolean pressed){

    }

    //glfwSetCursorEnterCallback
    private void mouseEntered(boolean entered){

    }



    //Other functions

    public void dispose(){
        keyCallback.free();
        mouseCallback.free();
        posCallback.free();
        scrollCallback.free();
        enterCallback.free();
    }

    public Window getWindow(){
        return window;
    }

}