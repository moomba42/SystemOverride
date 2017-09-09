package com.moomba.systemoverride.engine;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Model3D {
    private float[] positions;
    private float[] colors;
    private int positionBufferID;
    private int colorBufferID;

    private int vaoID;


    public Model3D(float[] positions, float[] colors) {
        this.positions = positions;
        this.colors = colors;
    }

    public void render(){
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_TRIANGLES, 0, positions.length/3);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public void uploadToGPU(){
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Position Attribute
        FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(positions.length);
        positionBuffer.put(positions).flip();

        positionBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionBufferID);
        glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //Color attribute
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorBuffer.put(colors).flip();

        colorBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        glBindVertexArray(0);
    }

    public void dispose(){
        glDeleteVertexArrays(vaoID);
        glDeleteBuffers(new int[]{positionBufferID});
    }
}
