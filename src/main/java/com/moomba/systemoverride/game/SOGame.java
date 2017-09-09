package com.moomba.systemoverride.game;

import com.moomba.systemoverride.engine.Engine;

public class SOGame{

    public static void main(String[] args){
        System.out.println("Starting System Override");
        Engine engine = new Engine();
        engine.run();
    }
}
