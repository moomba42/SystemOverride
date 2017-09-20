package com.moomba.systemoverride.engine.generation;

import com.moomba.systemoverride.engine.Mesh;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class DCMesher {

    private int triangleCount = 0;
    private int counter = 0;
    private Map<Octree.Node, Integer> indexMap;
    private List<Integer> indices;

    public Mesh generateMesh(Octree.Node root){
        indexMap = new HashMap<>();
        indices = new ArrayList<>();

        cellProc(root);

        Mesh mesh = extractMesh(indices, indexMap);
        mesh.uploadToGPU();

        return mesh;
    }

    private Mesh extractMesh(List<Integer> indicesList, Map<Octree.Node, Integer> indexMap) {
        float[] positons = new float[indexMap.size()*3];
        float[] normals = new float[indexMap.size()*3];
        float[] colors = new float[indexMap.size()*3];
        int[] indices = new int[indicesList.size()];

        indexMap.forEach((node, index)->{
            Vector3f pos = node.getQEFMinimizer().getPosition();
            Vector3f nor = node.getQEFMinimizer().getNormal();
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

        return new Mesh(positons, normals, colors, indices);
    }

    private void createQuad(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3){
        createTriangle(nd0, nd1, nd3);
        createTriangle(nd0, nd3, nd2);
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
        if(!nd0.isLeaf()){
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
        if(nd0.isLeaf() && nd1.isLeaf()) return;
        if (axis.equals(Axis.X)) {
            faceProc(childOrParent(nd0, 5), childOrParent(nd1, 4), Axis.X);
            faceProc(childOrParent(nd0, 7), childOrParent(nd1, 6), Axis.X);
            faceProc(childOrParent(nd0, 3), childOrParent(nd1, 2), Axis.X);
            faceProc(childOrParent(nd0, 1), childOrParent(nd1, 0), Axis.X);
            edgeProc(childOrParent(nd0, 5), childOrParent(nd1, 4), childOrParent(nd0, 7), childOrParent(nd1, 6),Axis.Z);
            edgeProc(childOrParent(nd0, 1), childOrParent(nd1, 0), childOrParent(nd0, 3), childOrParent(nd1, 2),Axis.Z);
            edgeProc(childOrParent(nd0, 7), childOrParent(nd0, 3), childOrParent(nd1, 6), childOrParent(nd1, 2),Axis.Y);
            edgeProc(childOrParent(nd0, 5), childOrParent(nd0, 1), childOrParent(nd1, 4), childOrParent(nd1, 0),Axis.Y);
        } else if (axis.equals(Axis.Y)) {
            faceProc(childOrParent(nd0, 6), childOrParent(nd1, 4), Axis.Y);
            faceProc(childOrParent(nd0, 2), childOrParent(nd1, 0), Axis.Y);
            faceProc(childOrParent(nd0, 3), childOrParent(nd1, 1), Axis.Y);
            faceProc(childOrParent(nd0, 7), childOrParent(nd1, 5), Axis.Y);
            edgeProc(childOrParent(nd0, 6), childOrParent(nd0, 2), childOrParent(nd1, 4), childOrParent(nd1, 0),Axis.X);
            edgeProc(childOrParent(nd0, 7), childOrParent(nd0, 3), childOrParent(nd1, 5), childOrParent(nd1, 1),Axis.X);
            edgeProc(childOrParent(nd0, 6), childOrParent(nd0, 7), childOrParent(nd1, 4), childOrParent(nd1, 5),Axis.Z);
            edgeProc(childOrParent(nd0, 2), childOrParent(nd0, 3), childOrParent(nd1, 0), childOrParent(nd1, 1),Axis.Z);
        } else if (axis.equals(Axis.Z)) {
            faceProc(childOrParent(nd0, 0), childOrParent(nd1, 4), Axis.Z);
            faceProc(childOrParent(nd0, 1), childOrParent(nd1, 5), Axis.Z);
            faceProc(childOrParent(nd0, 2), childOrParent(nd1, 6), Axis.Z);
            faceProc(childOrParent(nd0, 3), childOrParent(nd1, 7), Axis.Z);
            edgeProc(childOrParent(nd0, 2), childOrParent(nd1, 6), childOrParent(nd0, 3), childOrParent(nd1, 7),Axis.Y);
            edgeProc(childOrParent(nd0, 0), childOrParent(nd1, 4), childOrParent(nd0, 1), childOrParent(nd1, 5),Axis.Y);
            edgeProc(childOrParent(nd0, 0), childOrParent(nd1, 4), childOrParent(nd0, 2), childOrParent(nd1, 6),Axis.X);
            edgeProc(childOrParent(nd0, 1), childOrParent(nd1, 5), childOrParent(nd0, 3), childOrParent(nd1, 7),Axis.X);
        }
    }

    private void edgeProc(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3, Axis axis){
        if(nd0.isLeaf() && nd1.isLeaf() && nd2.isLeaf() && nd3.isLeaf()){
                classifyMinimalEdge(nd0, nd1, nd2, nd3, axis);
        }else if(axis.equals(Axis.X)){
            edgeProc(childOrParent(nd0, 3), childOrParent(nd1, 7), childOrParent(nd2, 1), childOrParent(nd3, 5), Axis.X);
            edgeProc(childOrParent(nd0, 2), childOrParent(nd1, 6), childOrParent(nd2, 0), childOrParent(nd3, 4), Axis.X);
        }else if(axis.equals(Axis.Y)){
            edgeProc(childOrParent(nd0, 1), childOrParent(nd1, 5), childOrParent(nd2, 0), childOrParent(nd3, 4), Axis.Y);
            edgeProc(childOrParent(nd0, 3), childOrParent(nd1, 7), childOrParent(nd2, 2), childOrParent(nd3, 6), Axis.Y);
        }else if(axis.equals(Axis.Z)){
            edgeProc(childOrParent(nd0, 7), childOrParent(nd1, 6), childOrParent(nd2, 5), childOrParent(nd3, 4), Axis.Z);
            edgeProc(childOrParent(nd0, 3), childOrParent(nd1, 2), childOrParent(nd2, 1), childOrParent(nd3, 0), Axis.Z);
        }
    }

    private void classifyMinimalEdge(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3, Axis axis) {
        if(nd0 != nd1 && nd1 != nd2 && nd2 != nd3 && nd3 != nd0 && nd0 != nd2 && nd1 != nd3 &&
           nd0.isTagged() && nd1.isTagged() && nd2.isTagged() && nd3.isTagged() &&
           edgeExhibitsSignChange(nd0, nd1, nd2, nd3, axis)) {
            createQuad(nd0, nd1, nd2, nd3);
        }
    }

    //TODO: Change this to work for different-sized nodes.
    private boolean edgeExhibitsSignChange(Octree.Node nd0, Octree.Node nd1, Octree.Node nd2, Octree.Node nd3, Axis axis) {
        double a = 0;
        double b = 0;
        if(axis.equals(Axis.X)){
            a = nd0.getHermiteData().getCornerSign(2);
            b = nd0.getHermiteData().getCornerSign(3);
        }
        if(axis.equals(Axis.Y)){
            a = nd1.getHermiteData().getCornerSign(7);
            b = nd1.getHermiteData().getCornerSign(5);
        }
        if(axis.equals(Axis.Z)){
            a = nd0.getHermiteData().getCornerSign(7);
            b = nd0.getHermiteData().getCornerSign(3);
        }

        return (a > 0 && b < 0) || (b > 0 && a < 0) || (a == 0 && b != 0) || (b == 0 && a != 0);
    }

    private Octree.Node childOrParent(Octree.Node parent, int child){
        if(parent.isLeaf()) return parent;
        else return parent.getChild(child);
    }
}
