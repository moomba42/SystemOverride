package com.moomba.systemoverride.engine.input;

import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import com.moomba.systemoverride.engine.Window;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class InputManager {

    //mainWindow listening on
    private Window mainWindow;

    //state
    private Vector2d mousePosition;
    private Map<Key, Boolean> keyStates;

    public InputManager(Window mainWindow){
        this.mainWindow = mainWindow;
        mousePosition = new Vector2d();
        keyStates = new HashMap<>();
        for(Key key : Key.values()){
            keyStates.put(key, false);
        }
    }

    //function for setting up callbacks
    public void init() {
        glfwSetMouseButtonCallback(mainWindow.getID(), (window, button, action, mods) ->{
            if(window == mainWindow.getID()){
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
        });

        glfwSetCursorPosCallback(mainWindow.getID(),(window, xPos, yPos)->{
                if(window == mainWindow.getID())
                    mousePosChanged(xPos, yPos);
        });

        glfwSetScrollCallback(mainWindow.getID(), (window, xOffset, yOffset) -> {
            if(window == InputManager.this.mainWindow.getID()) mouseScrolled(xOffset, yOffset);
        });

        glfwSetCursorEnterCallback(mainWindow.getID(), (window, entered) -> {
            if(window == mainWindow.getID()) mouseEntered(entered);
        });

        glfwSetKeyCallback(mainWindow.getID(), (window, keycode, scancode, action, mods) -> {
            if(window == mainWindow.getID()){
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
        });
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

    public Window getMainWindow(){
        return mainWindow;
    }

}