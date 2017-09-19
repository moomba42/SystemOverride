package com.moomba.systemoverride.engine.generation;

import org.joml.Vector3f;

/**
 * This octree is designed to serve as a structural backbone for the dual contouring algorithm, by being tagged with
 * {@link HermiteData}. The indexing of the children is represented by the awesome ascii art at the bottom of this
 * class's documentation. The child with the index 0 has cordinates (-size/2, -size/2, -size/2) relative to the parent's
 * center. The child with the index 7 has cordinates (size/2, size/2, size/2) relative to the parent's
 * center. Keep this in mind when trying to decode what the heck is going on in the {@link DCMesher}, {@link DCSimplifier}
 * and {@link HermiteDataGenerator}
 *
 *          +-------+-------+
 *         /   2   /   3   /|
 *        +-------+-------+ |            2-------3
 *       /   6   /   7   /|3|           /|      /|
 *      +-------+-------+ | +          / |     / |
 *      |       |       |7|/|         6-------7  |
 *      |   6   |   7   | + |         |  |    |  |
 *      |       |       |/|1|         |  0----|--1
 *      +-------+-------+ | +         | /     | /
 *      |       |       |5|/          4-------5
 *      |   4   |   5   | +
 *      |       |       |/
 *      +-------+-------+
 *
 * @author aleksander
 */
public class Octree {

    private Node rootNode;

    /**
     * Creates a new octree without children, only a root node. When meshing this octree, the actual mesh's center will
     * have the position substracted from the vertices, so the center is (0,0,0). That way there will be no problems with
     * rotating the mesh later on.
     * @param position The center of the octree.
     * @param size The edge size of the octree.
     */
    public Octree(Vector3f position, float size){
        rootNode = new Node(size, position);
    }

    /**
     * Overloaded constructor. Calls the {@link Octree#Octree(Vector3f, float)} constructor,
     * creating a new {@link Vector3f} behind the scenes.
     * @param x The x position of the center of the octree.
     * @param y The y position of the center of the octree.
     * @param z The z position of the center of the octree.
     * @param size The edge size of the octree.
     */
    public Octree(float x, float y, float z, float size){
        this(new Vector3f(x, y, z), size);
    }

    /**
     * Finds the desired {@link Node} by traversing the {@link Octree} through child indexes
     * @param path A sequence of numbers where the first number is the rootNode's child index,
     *             the second the child's child index and so on.
     * @return  The found node. If the loop hits a leafnode while not finished,
     *          it will return that node instead of the desired one.
     */
    public Node getNode(int... path){
        if(path == null || path.length == 0)
            return rootNode;

        Node node = rootNode.getChild(path[0]);
        for (int i = 1; i < path.length; i++) {
            if(node.isLeaf()) break;
            node = node.getChild(path[i]);
        }

        return node;
    }

    /**
     * Traverses the octree, calling the processor's {@link NodeProcessor#processNode(Node)} on each leaf node that it finds.
     * @param processor The object that will be fed all the leaf nodes in this octree.
     */
    public void forEachLeaf(NodeProcessor processor){
        rootNode.processLeafs(processor);
    }

    /**
     * Traverses the octree, calling {@link Node#subdivide()} on each leaf node that it finds.
     */
    public void simplifyLeafs(){
        rootNode.processLeafs(Node::subdivide);
    }

    public class Node{

        private float edgeSize;
        private Vector3f center;
        private HermiteData hermiteData;
        private FunctionIntersection qefMinimizer;
        private Node[] children;

        /**
         * Creates a octree node.
         * @param edgeSize The total distance of opposite faces. Or in other words, 2x the distance from
         *                 the center to each of the faces.
         * @param center The center of the node.
         */
        public Node(float edgeSize, Vector3f center){
            this.edgeSize = edgeSize;
            this.center = center;
        }

        /**
         * Initializes the children array with the size of 8 ( one for each corner ). Then it proceeds to fill the array
         * with new {@link Node}s, giving them the edgeSize of the parent divided by two, and the center of the parent but shifted
         * by 1/4 ( not 1/2 because 1/2 is the distance from the center to the edge, but the node already is 1/2 of the
         * parent's size, so the remaining space is 1/4) of the parent's edgesize towards its respective corner.
         */
        public void subdivide(){
            float offset = edgeSize/4;
            children = new Node[8];
            children[0] = new Node(edgeSize/2, new Vector3f(center).add(-offset, -offset, -offset));
            children[1] = new Node(edgeSize/2, new Vector3f(center).add(+offset, -offset, -offset));
            children[2] = new Node(edgeSize/2, new Vector3f(center).add(-offset, +offset, -offset));
            children[3] = new Node(edgeSize/2, new Vector3f(center).add(+offset, +offset, -offset));
            children[4] = new Node(edgeSize/2, new Vector3f(center).add(-offset, -offset, +offset));
            children[5] = new Node(edgeSize/2, new Vector3f(center).add(+offset, -offset, +offset));
            children[6] = new Node(edgeSize/2, new Vector3f(center).add(-offset, +offset, +offset));
            children[7] = new Node(edgeSize/2, new Vector3f(center).add(+offset, +offset, +offset));
        }

         /**
         * A recursive function that is supposed to process every existing leaf node of this node.
         * If this node is a leaf node then {@link NodeProcessor#processNode(Node)} is called on this node. Otherwise
         * the function loops through all children of this node and calls the {@link Node#processLeafs(NodeProcessor)} function
         * (itself) on the children, passing the given {@link NodeProcessor} object.
         * @param processor The processor that will process all leaf nodes originating from this node,
         *                  or this node if its a leaf node.
         */
        public void processLeafs(NodeProcessor processor){
            if(isLeaf()) {
                processor.processNode(this);
            } else {
                for (Node child : children) {
                    child.processLeafs(processor);
                }
            }
        }

        /**
         * Checks if children array is null.
         * @return Whenever this node is a leaf node ( has no children ).
         */
        public boolean isLeaf(){
            return children == null;
        }

        /**
         * Look at {@link Octree} documentation to figure out the index of the desired child.
         * @param index The index of the child
         * @return The desired child.
         */
        public Node getChild(int index){
            return children[index];
        }
        /**
         * Look at {@link Octree} documentation to figure out the index of the desired corner.
         * @param index The index of the corner
         * @return The desired corner.
         */
        public Vector3f getCornerPosition(int index){
            return new Vector3f(
                    center.x + ((index & 1) == 1 ? edgeSize/2 : -edgeSize/2),
                    center.y + ((index & 2) == 2 ? edgeSize/2 : -edgeSize/2),
                    center.z + ((index & 4) == 4 ? edgeSize/2 : -edgeSize/2)
            );
        }

        /**
         * Returns the associated hermite data.
         * @return The associated hermite data. May be null.
         */
        public HermiteData getHermiteData(){
            return hermiteData;
        }

        /**
         * Checks if there is any hermite data associated with this node
         * @return Whenever this node is tagged by hermite data.
         */
        public boolean isTagged(){
            return hermiteData != null;
        }

        /**
         * Tags this node with hermite data.
         * @param hermiteData The hermite data to be associated with this node.
         */
        public void tagWithHermiteData(HermiteData hermiteData){
            this.hermiteData = hermiteData;
        }

        /**
         * Removes the associated hermite data from this node.
         */
        public void removeHermiteData(){
            this.hermiteData = null;
        }

        public Vector3f getCenter() {
            return center;
        }

        public FunctionIntersection getQEFMinimizer() {
            return qefMinimizer;
        }

        public void setQEFMinimizer(FunctionIntersection qefMinimizer) {
            this.qefMinimizer = qefMinimizer;
        }

        public float getEdgeSize() {
            return edgeSize;
        }
    }

    @FunctionalInterface
    public interface NodeProcessor{
        void processNode(Node node);
    }
}
