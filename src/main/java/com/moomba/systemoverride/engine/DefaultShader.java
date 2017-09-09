package com.moomba.systemoverride.engine;

import java.io.File;

public class DefaultShader extends Shader{

    public DefaultShader() throws Exception {
        super(
                new File(DefaultShader.class.getClassLoader().getResource("defaultShader.vert").getPath()),
                new File(DefaultShader.class.getClassLoader().getResource("defaultShader.frag").getPath()));
    }
}
