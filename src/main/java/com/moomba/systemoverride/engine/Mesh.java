package com.moomba.systemoverride.engine;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {

    private float[] positions;
    private float[] normals;
    private float[] colors;
    private int[] indices;

    private int positionBufferID;
    private int normalBufferID;
    private int colorBufferID;
    private int indexBufferID;

    private int vaoID;

    private int drawMode = GL_TRIANGLES;


    public Mesh(float[] positions, float[] normals, float[] colors, int[] indices) {
        this.positions = positions;
        this.normals = normals;
        this.colors = colors;
        this.indices = indices;
    }

    public void setDrawMode(int drawMode){
        this.drawMode = drawMode;
    }

    public void bind(){
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    public void render(){
        glDrawElements(
                drawMode,
                indices.length,
                GL_UNSIGNED_INT,
                0
        );
    }

    public void unbind(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(3);
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

        //Normals attribute
        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.length);
        normalBuffer.put(normals).flip();

        normalBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalBufferID);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, true, 0, 0);

        //Color attribute
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorBuffer.put(colors).flip();

        colorBufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        //Indices attribute
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        indexBufferID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public void dispose(){
        glDeleteVertexArrays(vaoID);
        glDeleteBuffers(new int[]{positionBufferID, normalBufferID, colorBufferID, indexBufferID});
    }
}
