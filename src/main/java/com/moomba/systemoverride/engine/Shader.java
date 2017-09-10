package com.moomba.systemoverride.engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final int program;
    private final int vertex;
    private final int fragment;
    protected String log;


    public Shader(File vertexFile, File fragmentFile) throws Exception {
        this(loadText(vertexFile), loadText(fragmentFile));
    }

    /**
     * Creates a new shader from vertex and fragment source, and with the given
     * map of <Integer, String> attrib locations
     * @param vertexShader the vertex shader source string
     * @param fragmentShader the fragment shader source string
     * @throws Exception if the program could not be compiled and linked
     */
    public Shader(String vertexShader, String fragmentShader) throws Exception {
        //compile the String source
        vertex = compileShader(vertexShader, GL_VERTEX_SHADER);
        fragment = compileShader(fragmentShader, GL_FRAGMENT_SHADER);

        //create the program
        program = glCreateProgram();

        //attach the shaders
        glAttachShader(program, vertex);
        glAttachShader(program, fragment);

        //link our program
        glLinkProgram(program);

        //grab our info log
        String infoLog = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));

        //if some log exists, append it
        if (infoLog.trim().length()!=0)
            log += infoLog;

        //if the link failed, throw some sort of exception
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
            throw new Exception(
                    "Failure in linking program. Error log:\n" + infoLog);

        //detach and delete the shaders which are no longer needed
        glDetachShader(program, vertex);
        glDetachShader(program, fragment);
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    /** Compile the shader source as the given type and return the shader object ID. */
    private int compileShader(String source, int type) throws Exception {
        //create a shader object
        int shader = glCreateShader(type);
        //pass the source string
        glShaderSource(shader, source);
        //compile the source
        glCompileShader(shader);

        //if info/warnings are found, append it to our shader log
        String infoLog = glGetShaderInfoLog(shader,
                glGetShaderi(shader, GL_INFO_LOG_LENGTH));
        if (infoLog.trim().length()!=0)
            log += getName(type) +": "+infoLog + "\n";

        //if the compiling was unsuccessful, throw an exception
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new Exception("Failure in compiling " + getName(type)
                    + ". Error log:\n" + infoLog);

        return shader;
    }

    private String getName(int shaderType) {
        if (shaderType == GL_VERTEX_SHADER)
            return "GL_VERTEX_SHADER";
        if (shaderType == GL_FRAGMENT_SHADER)
            return "GL_FRAGMENT_SHADER";
        else
            return "shader";
    }

    /**
     * Make this shader the active program.
     */
    public void use() {
        glUseProgram(program);
    }

    /**
     * Destroy this shader program.
     */
    public void dispose() {
        glDeleteProgram(program);
    }

    /**
     * Gets the location of the specified uniform name.
     * @param str the name of the uniform
     * @return the location of the uniform in this program
     */
    public int getUniformLocation(String str) {
        return glGetUniformLocation(program, str);
    }

    // --[ Uniform setters ]-- //

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void loadInt(int location, int value) {
        glUniform1i(location, value);
    }

    protected void load2DVector(int location, Vector2f vector) {
        glUniform2f(location, vector.x, vector.y);
    }

    protected void load3DVector(int location, Vector3f vector) {
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void load4DVector(int location, Vector4f vector) {
        glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    protected void loadBoolean(int location, boolean value) {
        glUniform1f(location, value ? 1 : 0);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    private static String loadText(File file){
        try {
            String text = "";
            for (String line : Files.readAllLines(file.toPath())) {
                text = text + line + "//\n";
            }
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}