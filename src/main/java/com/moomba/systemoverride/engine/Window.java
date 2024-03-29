package com.moomba.systemoverride.engine;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeLimits;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GLCapabilities;

public class Window {

    private long id;
    private int width, height;
    private String title;
    private boolean resizable, fullscreen, vsync, visible;

    private List<ResizeListener> resizeListeners;

    public Window(int width, int height, String title, boolean resizable, boolean fullscreen, boolean vsync){
        this.width = width;
        this.height = height;
        this.title = title;
        this.resizable = resizable;
        this.fullscreen = fullscreen;
        this.visible = true;
        resizeListeners = new ArrayList<>();
    }

    public void init(){
        //set the error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //set the actual window window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        //create the window and store its id in the variable id
        id = GLFW.glfwCreateWindow(width, height, title, fullscreen ? GLFW.glfwGetPrimaryMonitor() : NULL, NULL);
        if (id == NULL){
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //position the window in the center
        centerWindow();

        glfwMakeContextCurrent(id);

        createCapabilities();

        setVsync(vsync);

        //setup the resize callback listener to call the onResize function on resize
        glfwSetFramebufferSizeCallback(getID(), (window, newWidth, newHeight) -> {
            if(window == getID())
                onResize(newWidth, newHeight);
        });
    }

    public void update(){
        glfwSwapBuffers(getID());
        glfwPollEvents();
    }

    public void centerWindow(){
        //get the current monitor video mode
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        //get this window's size (no, you cant bind the fields width & height - would cause problems with retina)
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(getID(), w, h);
        int width = w.get(0);
        int height = h.get(0);

        //position the window at the center of the screen
        glfwSetWindowPos(getID(), (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public void setVisiblity(boolean visible) {
        this.visible = visible;
        if(visible){
            glfwShowWindow(getID());
        }else{
            glfwHideWindow(getID());
        }
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
        glfwSetWindowTitle(getID(), newTitle);
    }

    public void setClipboardString(String string){
        glfwSetClipboardString(getID(), string);
    }

    public void setSize(int newWidth, int newHeight){
        this.width = newWidth;
        this.height = newHeight;
        glfwSetWindowSize(getID(), newWidth, newHeight);
    }

    public void lockMouse(){
        glfwSetInputMode(getID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void unlockMouse(){
        glfwSetInputMode(getID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight){
        glfwSetWindowSizeLimits(getID(), minWidth, minHeight, maxWidth, maxHeight);
    }

    public void addResizeListener(ResizeListener listener){
        resizeListeners.add(listener);
    }

    public void removeResizeListener(ResizeListener listener){
        resizeListeners.remove(listener);
    }

    private void onResize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        resizeListeners.forEach((listener)->{listener.onResize(newWidth, newHeight);});
    }

    public long getID(){
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isResizable() {
        return resizable;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public boolean isVsync() {
        return vsync;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(getID());
    }


    public void destroy() {
        glfwFreeCallbacks(getID());
        glfwDestroyWindow(getID());
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    @FunctionalInterface
    public interface ResizeListener {
        void onResize(int width, int height);
    }

}
