package com.moomba.systemoverride.engine.generation;

import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

@SuppressWarnings("Duplicates")
public class HermiteDataGenerator {

    private final int[] cornerEdgePairs = new int[]{
            0,2,  1,3,  5,7,  4,6, //y axis
            2,3,  6,7,  4,5,  0,1, //x axis
            2,6,  3,7,  1,5,  0,4  //z axis
    };

    public Octree.Node generateHermiteDataFor(Octree.Node node, Function function){
        node.processLeafs(leaf ->{
            HermiteData hermiteData = new HermiteData();

            //calculate the sign for each corner of the cube and store it in the signs array
            for(int i = 0; i < 8; i++){
                Vector3f corner = leaf.getCornerPosition(i);
                hermiteData.setCornerSign(i, (float) function.noise(corner.x, corner.y, corner.z));
            }

            if()

            //for each edge that exhibits a sign change, generate a function intersection point and normal, then store
            //it in the hermite data object
            for(int i = 0; i < cornerEdgePairs.length; i+=2){
                Vector3f cornerA = leaf.getCornerPosition(cornerEdgePairs[ i ]);
                Vector3f cornerB = leaf.getCornerPosition(cornerEdgePairs[i+1]);
                double cornerAsign = hermiteData.getCornerSign(cornerEdgePairs[ i ]);
                double cornerBsign = hermiteData.getCornerSign(cornerEdgePairs[i+1]);
                if(changesSign(cornerAsign, cornerBsign)) {// if the edge intersects the 0 surface of the function
                    Vector3f intersectionPoint = getIntersectionPoint(cornerA, cornerAsign, cornerB, cornerBsign);
                    Vector3f intersectionNormal = toVector3f(function.normal(intersectionPoint.x, intersectionPoint.y, intersectionPoint.z));
                    FunctionIntersection functionIntersection = new FunctionIntersection(intersectionPoint, intersectionNormal);
                    hermiteData.getFunctionIntersections().add(functionIntersection);
                }
            }

            //for each corner calculate a qef and store it in the hermite data object
            for (int i = 0; i < 8; i++) {
                hermiteData.setCornerQEF(i, (float) getQEF(
                            leaf.getCornerPosition(i),
                            hermiteData.getFunctionIntersections()));
            }

            //calculate the qef for the center of the cube
            hermiteData.setCenterQEF((float) getQEF(
                    leaf.getCenter(),
                    hermiteData.getFunctionIntersections()));

            leaf.tagWithHermiteData(hermiteData);
        });

        return node;
    }

    private Vector3f toVector3f(Vector3d normal) {
        return new Vector3f((float) normal.x, (float) normal.y, (float) normal.z);
    }


    private Vector3f getIntersectionPoint(Vector3f cornerA, double cornerAsign, Vector3f cornerB, double cornerBsign) {
        return cornerA.lerp(
                cornerB, //the other point to transition to
                (float) (Math.abs(cornerAsign) / (Math.abs(cornerAsign)+Math.abs(cornerBsign))), //distance (%)
                new Vector3f()); //destination vector
    }

    private boolean changesSign(double a, double b) {
        return (a > 0 && b < 0)   || (a < 0 && b > 0) ||
               (a == 0 && b != 0) || (b == 0 && a != 0);
    }

    private double getQEF(Vector3f center, List<FunctionIntersection> intersections){
        double QEF = 0.0;
        for (int i = 0; i < intersections.size(); i++) {
            FunctionIntersection functionIntersection = intersections.get(i);
            Vector3f pos = functionIntersection.getPosition();
            Vector3f norm = functionIntersection.getNormal();
            double denom = norm.x * norm.x + norm.y * norm.y + norm.z * norm.z;
            double d = -norm.x * pos.x - norm.y * pos.y - norm.z * pos.z;
            double nomSqrt = (norm.x * center.x + norm.y * center.y + norm.z * center.z + d);
            double distSq = nomSqrt*nomSqrt / denom;
            QEF += distSq;
        }
        return QEF;
    }

}
