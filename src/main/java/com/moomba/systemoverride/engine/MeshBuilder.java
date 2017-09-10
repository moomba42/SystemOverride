package com.moomba.systemoverride.engine;

public class MeshBuilder {


    public static Mesh axes(float size){
        float s = size;
        float c = 0.05f*s; //center edge size
        float h = c/2f; // half center edge size

        float[] positions = new float[]{
            0, 0, c, // 0
            c, 0, c, // 1
            c, 0, 0, // 2
            c, c, 0, // 3
            0, c, 0, // 4
            0, c, c, // 5
            c, c, c, // 6
            s, h, h, // 7
            h, s, h, // 8
            h, h, s  // 9
        };

        float[] normals = new float[]{
           -1, -1,  0, // 0
            1, -1,  1, // 1
            1, -1, -1, // 2
            1,  1, -1, // 3
           -1,  1, -1, // 4
           -1,  1,  1, // 5
            1,  1,  1, // 6
            1,  0,  0, // 7
            0,  1,  0, // 8
            0,  0,  1  // 9
        };

        float[] colors = new float[]{
            0, 0, 1, // 0
            1, 0, 1, // 1
            1, 0, 0, // 2
            1, 1, 0, // 3
            0, 1, 0, // 4
            0 ,1, 1, // 5
            1, 0, 1, // 6
            1, 0, 0, // 7
            0, 1, 0, // 8
            0, 0, 1  // 9
        };

        int[] indices = new int[]{
            1, 2, 7, // R
            2, 3, 7, // R
            3, 6, 7, // R
            6, 1, 7, // R

            6, 3, 8, // G
            3, 4, 8, // G
            4, 5, 8, // G
            5, 6, 8, // G

            5, 9, 6, // B
            5, 0, 9, // B
            0, 1, 9, // B
            1, 6, 9  // B
        };

        Mesh mesh = new Mesh(positions, normals, colors, indices);
        mesh.uploadToGPU();

        return mesh;
    }
}
