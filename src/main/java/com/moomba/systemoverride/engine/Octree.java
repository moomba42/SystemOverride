package com.moomba.systemoverride.engine;

import org.joml.Vector3f;

public class Octree {

    private Node root;

    public Octree(float size){
        root = new Node(0, size, new Vector3f(0, 0, 0));
    }

    public Node getRoot() {
        return root;
    }

    public void subdivide(){
        root.subdivide();
    }

    public void processLeafs(NodeProcessor processor){
        root.processLeafs(processor);
    }

    @FunctionalInterface
    public interface NodeProcessor{
        void process(Node node);
    }

    public static class Node{
        public float edgeSize;
        public int depth;
        public float[] signs;
        public Node[] children;
        public Node[] neighbours;
        public Vector3f center;
        public Vector3f vertex;
        public Vector3f normal;
        public double QEF;

        public Node(int depth, float edgeSize, Vector3f center){
            this.depth = depth;
            this.edgeSize = edgeSize;
            this.center = center;
            this.signs = new float[8];
        }

        public void subdivide(){
            if(children == null){
                float hEdgeSize = edgeSize/4;
                children = new Node[8];
                children[0] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(-hEdgeSize, -hEdgeSize, -hEdgeSize));
                children[1] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(+hEdgeSize, -hEdgeSize, -hEdgeSize));
                children[2] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(-hEdgeSize, +hEdgeSize, -hEdgeSize));
                children[3] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(+hEdgeSize, +hEdgeSize, -hEdgeSize));
                children[4] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(-hEdgeSize, -hEdgeSize, +hEdgeSize));
                children[5] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(+hEdgeSize, -hEdgeSize, +hEdgeSize));
                children[6] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(-hEdgeSize, +hEdgeSize, +hEdgeSize));
                children[7] = new Node(depth+1, edgeSize/2, new Vector3f(center).add(+hEdgeSize, +hEdgeSize, +hEdgeSize));
            }else{
                for (Node child : children) {
                    child.subdivide();
                }
            }
        }

        public void tag(Vector3f vertex, Vector3f normal){
            this.vertex = vertex;
            this.normal = normal;
            children = null;
        }

        public void setNeighbours(Node[] neighbours){
            this.neighbours = neighbours;
        }

        public Node[] getNeighbours(){
            return neighbours;
        }

        public Node[] getChildren(){
            return children;
        }

        public boolean isTagged(){
            return vertex != null && normal != null;
        }

        public float getEdgeSize() {
            return edgeSize;
        }

        public int getDepth() {
            return depth;
        }

        public Vector3f getVertex() {
            return vertex;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public void processLeafs(NodeProcessor processor) {
            if(children == null) processor.process(this);
            else for (Node child : children) {
                child.processLeafs(processor);
            }
        }

        public boolean hasChildren() {
            return children != null;
        }

        public Node getChild(int i) {
            return children[i];
        }

        public double getSign(int i){
            return signs[i];
        }

        public void setSign(int i, float value){
            signs[i] = value;
        }

        public Vector3f getCenter() {
            return center;
        }

        public Vector3f getCorner(int i){
            return new Vector3f(
                    center.x + ((i & 1) == 1 ? edgeSize/2 : -edgeSize/2),
                    center.y + ((i & 2) == 2 ? edgeSize/2 : -edgeSize/2),
                    center.z + ((i & 4) == 4 ? edgeSize/2 : -edgeSize/2)
            );
        }

        public double getQEF() {
            return QEF;
        }

        public void setQEF(double QEF) {
            this.QEF = QEF;
        }


    }
}
