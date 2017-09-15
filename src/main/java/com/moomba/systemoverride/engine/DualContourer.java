package com.moomba.systemoverride.engine;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DualContourer {

    private int triangleCount = 0;

    private int counter = 0;
    private Map<Octree.Node, Integer> indexMap;
    private List<Integer> indices;

    public Mesh contoure(Octree octree){
        indexMap = new HashMap<>();
        indices = new ArrayList<>();

        // TODO: 1. Collapse homogenous leaves.
        // TODO: 2. Construct a QEF for each heterogenous leaf and simplify the octree using these QEFs.
        // 3. Recursively generate polygons for this simplified octree.
        cellProc(octree.getRoot());
        System.out.println("Triangle Count: "+triangleCount);

        float[] positons = new float[indexMap.size()*3];
        float[] normals = new float[indexMap.size()*3];
        float[] colors = new float[indexMap.size()*3];
        int[] indices = new int[this.indices.size()];

        indexMap.forEach((node, index)->{
            Vector3f pos = node.getVertex();
            Vector3f nor = node.getNormal();
            positons[(index*3)+0] = pos.x;
            positons[(index*3)+1] = pos.y;
            positons[(index*3)+2] = pos.z;
            normals[(index*3)+0] = nor.x;
            normals[(index*3)+1] = nor.y;
            normals[(index*3)+2] = nor.z;
            colors[(index*3)+0] = 1.0f;
            colors[(index*3)+1] = 0.0f;
            colors[(index*3)+2] = 0.0f;
        });

        for (int i = 0; i < this.indices.size(); i++) {
            indices[i] = this.indices.get(i);
        }

        Mesh mesh = new Mesh(positons, normals, colors, indices);
        mesh.uploadToGPU();

        return mesh;
    }

    private void createQuad(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3){
        createTriangle(nd0, nd3, nd2);
        createTriangle(nd0, nd1, nd3);
    }

    private void createTriangle(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2){
        triangleCount++;
        Integer nd0i = getIndice(nd0);
        Integer nd1i = getIndice(nd1);
        Integer nd2i = getIndice(nd2);
        indices.add(nd0i);
        indices.add(nd1i);
        indices.add(nd2i);
    }

    private int getIndice(Octree.Node nd0) {
        if(indexMap.containsKey(nd0)){
            return indexMap.get(nd0);
        }else{
            int newIndex = counter;
            indexMap.put(nd0, newIndex);
            counter++;
            return newIndex;
        }
    }

    private void cellProc(Octree.Node nd0){
        if(nd0.hasChildren()){
            cellProc(nd0.getChild(0));
            cellProc(nd0.getChild(1));
            cellProc(nd0.getChild(2));
            cellProc(nd0.getChild(3));
            cellProc(nd0.getChild(4));
            cellProc(nd0.getChild(5));
            cellProc(nd0.getChild(6));
            cellProc(nd0.getChild(7));
            faceProc(nd0.getChild(0), nd0.getChild(1), Axis.X);
            faceProc(nd0.getChild(2), nd0.getChild(3), Axis.X);
            faceProc(nd0.getChild(4), nd0.getChild(5), Axis.X);
            faceProc(nd0.getChild(6), nd0.getChild(7), Axis.X);
            faceProc(nd0.getChild(0), nd0.getChild(2), Axis.Y);
            faceProc(nd0.getChild(1), nd0.getChild(3), Axis.Y);
            faceProc(nd0.getChild(4), nd0.getChild(6), Axis.Y);
            faceProc(nd0.getChild(5), nd0.getChild(7), Axis.Y);
            faceProc(nd0.getChild(4), nd0.getChild(0), Axis.Z);
            faceProc(nd0.getChild(5), nd0.getChild(1), Axis.Z);
            faceProc(nd0.getChild(6), nd0.getChild(2), Axis.Z);
            faceProc(nd0.getChild(7), nd0.getChild(3), Axis.Z);
            edgeProc(nd0.getChild(4), nd0.getChild(0), nd0.getChild(6), nd0.getChild(2), Axis.X);
            edgeProc(nd0.getChild(5), nd0.getChild(1), nd0.getChild(7), nd0.getChild(3), Axis.X);
            edgeProc(nd0.getChild(6), nd0.getChild(2), nd0.getChild(7), nd0.getChild(3), Axis.Y);
            edgeProc(nd0.getChild(4), nd0.getChild(0), nd0.getChild(5), nd0.getChild(1), Axis.Y);
            edgeProc(nd0.getChild(0), nd0.getChild(1), nd0.getChild(2), nd0.getChild(3), Axis.Z);
            edgeProc(nd0.getChild(4), nd0.getChild(5), nd0.getChild(6), nd0.getChild(7), Axis.Z);
        }
    }

    private void faceProc(Octree.Node nd0, Octree.Node nd1, Axis axis){
        if(nd0.hasChildren() || nd1.hasChildren()) {
            if (axis.equals(Axis.X)) {
                faceProc(childOrParent(nd0, 5), childOrParent(nd1, 4), Axis.X);
                faceProc(childOrParent(nd0, 6), childOrParent(nd1, 7), Axis.X);
                faceProc(childOrParent(nd0, 2), childOrParent(nd1, 3), Axis.X);
                faceProc(childOrParent(nd0, 0), childOrParent(nd1, 1), Axis.X);
                edgeProc(childOrParent(nd0, 5), childOrParent(nd1, 4), childOrParent(nd0, 7), childOrParent(nd1, 6), Axis.Z);
                edgeProc(childOrParent(nd0, 1), childOrParent(nd1, 0), childOrParent(nd0, 3), childOrParent(nd1, 2), Axis.Z);
                edgeProc(childOrParent(nd0, 7), childOrParent(nd1, 3), childOrParent(nd0, 6), childOrParent(nd1, 2), Axis.Y);
                edgeProc(childOrParent(nd0, 5), childOrParent(nd1, 1), childOrParent(nd0, 4), childOrParent(nd1, 0), Axis.Y);
            } else if (axis.equals(Axis.Y)) {
                faceProc(childOrParent(nd0, 6), childOrParent(nd1, 4), Axis.X);
                faceProc(childOrParent(nd0, 2), childOrParent(nd1, 0), Axis.X);
                faceProc(childOrParent(nd0, 3), childOrParent(nd1, 1), Axis.X);
                faceProc(childOrParent(nd0, 7), childOrParent(nd1, 5), Axis.X);
                edgeProc(childOrParent(nd0, 6), childOrParent(nd1, 2), childOrParent(nd0, 4), childOrParent(nd1, 0), Axis.X);
                edgeProc(childOrParent(nd0, 7), childOrParent(nd1, 3), childOrParent(nd0, 5), childOrParent(nd1, 1), Axis.X);
                edgeProc(childOrParent(nd0, 6), childOrParent(nd1, 7), childOrParent(nd0, 4), childOrParent(nd1, 5), Axis.Z);
                edgeProc(childOrParent(nd0, 2), childOrParent(nd1, 3), childOrParent(nd0, 0), childOrParent(nd1, 1), Axis.Z);
            } else if (axis.equals(Axis.Z)) {
                faceProc(childOrParent(nd0, 0), childOrParent(nd1, 4), Axis.X);
                faceProc(childOrParent(nd0, 1), childOrParent(nd1, 5), Axis.X);
                faceProc(childOrParent(nd0, 2), childOrParent(nd1, 6), Axis.X);
                faceProc(childOrParent(nd0, 3), childOrParent(nd1, 7), Axis.X);
                edgeProc(childOrParent(nd0, 2), childOrParent(nd1, 6), childOrParent(nd0, 3), childOrParent(nd1, 7), Axis.Y);
                edgeProc(childOrParent(nd0, 0), childOrParent(nd1, 4), childOrParent(nd0, 1), childOrParent(nd1, 5), Axis.Y);
                edgeProc(childOrParent(nd0, 0), childOrParent(nd1, 4), childOrParent(nd0, 2), childOrParent(nd1, 6), Axis.X);
                edgeProc(childOrParent(nd0, 1), childOrParent(nd1, 5), childOrParent(nd0, 3), childOrParent(nd1, 7), Axis.X);
            }
        }
    }

    private void edgeProc(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3, Axis axis){
        if(nd0.hasChildren() || nd1.hasChildren() || nd2.hasChildren() || nd3.hasChildren()){
            if(axis.equals(Axis.X)){
                edgeProc(childOrParent(nd0, 3), childOrParent(nd1, 7), childOrParent(nd2, 1), childOrParent(nd3, 5), Axis.X);
                edgeProc(childOrParent(nd0, 2), childOrParent(nd1, 6), childOrParent(nd2, 0), childOrParent(nd3, 4), Axis.X);
            }else if(axis.equals(Axis.Y)){
                edgeProc(childOrParent(nd0, 1), childOrParent(nd1, 5), childOrParent(nd2, 0), childOrParent(nd3, 4), Axis.X);
                edgeProc(childOrParent(nd0, 3), childOrParent(nd1, 7), childOrParent(nd2, 2), childOrParent(nd3, 6), Axis.X);
            }else if(axis.equals(Axis.Z)){
                edgeProc(childOrParent(nd0, 7), childOrParent(nd1, 6), childOrParent(nd2, 5), childOrParent(nd3, 4), Axis.Z);
                edgeProc(childOrParent(nd0, 3), childOrParent(nd1, 2), childOrParent(nd2, 1), childOrParent(nd3, 0), Axis.Z);
            }
        }else{
            classifyMinimalEdge(nd0, nd1, nd2, nd3);
        }
    }

    private void classifyMinimalEdge(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3) {
        if(nd0 == nd1) classifyMinimalEdge(nd0, nd2, nd3);
        else if(nd1 == nd2) classifyMinimalEdge(nd0, nd1, nd3);
        else if(nd2 == nd3) classifyMinimalEdge(nd0, nd1, nd2);
        else if(nd3 == nd0) classifyMinimalEdge(nd0, nd1, nd2);
        else if(nd0 == nd2) classifyMinimalEdge(nd0, nd1, nd3);
        else if(nd1 == nd3) classifyMinimalEdge(nd0, nd1, nd2);
        else if(!nd0.isTagged()) classifyMinimalEdge(nd1, nd2, nd3);
        else if(!nd1.isTagged()) classifyMinimalEdge(nd0, nd2, nd3);
        else if(!nd2.isTagged()) classifyMinimalEdge(nd0, nd1, nd3);
        else if(!nd3.isTagged()) classifyMinimalEdge(nd0, nd1, nd2);
        else createQuad(nd0, nd1, nd2, nd3);
    }

    private void classifyMinimalEdge(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2) {
        if(nd0 != nd1 && nd1 != nd2 && nd2 != nd0 && nd0.isTagged() && nd1.isTagged() && nd2.isTagged())
            createTriangle(nd0, nd1, nd2);
    }

    private Octree.Node childOrParent(Octree.Node parent, int child){
        if(parent.hasChildren()) return parent.getChild(child);
        else return parent;
    }

    private enum Axis{
        X,
        Y,
        Z
    }
}
