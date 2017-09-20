package com.moomba.systemoverride.engine.generation;

import org.joml.*;
import org.joml.Math;

import java.util.List;

@SuppressWarnings("Duplicates")
public class QEFMinimizer {

    public void minimizeQEFsForTaggedNodes(Octree.Node root, Function function){
        root.processLeafs(node -> {
            if(node.isTagged()) {
                Vector3f minimizer = minimizeQEF2(node);
                Vector3d normald = function.normal(minimizer.x, minimizer.y, minimizer.z);
                Vector3f normal = new Vector3f((float) normald.x, (float) normald.y, (float) normald.z);
                FunctionIntersection functionIntersection = new FunctionIntersection(minimizer, normal);
                node.setQEFMinimizer(functionIntersection);
            }
        });
    }

    private Vector3f minimizeQEF2(Octree.Node node) {
        Vector3f[] corners = new Vector3f[]{
                node.getCornerPosition(0).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(1).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(2).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(3).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(4).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(5).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(6).lerp(node.getCenter(), 0.01f),
                node.getCornerPosition(7).lerp(node.getCenter(), 0.01f)
        };
        double[] qefs = new double[]{
                getQEF(corners[0], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[1], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[2], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[3], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[4], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[5], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[6], node.getHermiteData().getFunctionIntersections()),
                getQEF(corners[7], node.getHermiteData().getFunctionIntersections())
        };
        double qefv = getQEF(node.getCenter(), node.getHermiteData().getFunctionIntersections());
        double oqefv = qefv;
        Vector3f vertex = new Vector3f(node.getCenter());
        float iterations = 10;
        for (int i = 0; i < iterations; i++) {
            for (int i1 = 0; i1 < corners.length; i1++) {
                if((qefv < 0 && oqefv > 0) || (qefv > 0 && oqefv < 0) || (qefv == 0 && oqefv != 0) || (qefv != 0 && oqefv == 0))
                    break;
                Vector3f corner = corners[i1];
                double qef = qefs[i1];
                double distance = (Math.abs(qef)+Math.abs(qefv));
                float lerp = (float) (qefv/distance);
                vertex.lerp(corner, (lerp*lerp));
                qefv = getQEF(vertex, node.getHermiteData().getFunctionIntersections());
            }
        }
        return vertex;
    }

    private Vector3f minimizeQEF(Octree.Node node) {
        float divisions = 16;
        float stepSize = node.getEdgeSize()/divisions;
        double smallestQEF = 99999;
        Vector3f smallestQEFCenter = new Vector3f();
        for(float ix = 0; ix < divisions; ix++){
            for(float iy = 0; iy < divisions; iy++) {
                for (float iz = 0; iz < divisions; iz++) {
                    Vector3f cellCenter = new Vector3f(
                            node.getCenter().x-(node.getEdgeSize()/2)+(ix*stepSize)+(stepSize/2),
                            node.getCenter().y-(node.getEdgeSize()/2)+(iy*stepSize)+(stepSize/2),
                            node.getCenter().z-(node.getEdgeSize()/2)+(iz*stepSize)+(stepSize/2)
                    );
                    double QEF = getQEF(cellCenter, node.getHermiteData().getFunctionIntersections());
                    if(QEF < smallestQEF){
                        smallestQEF = QEF;
                        smallestQEFCenter = cellCenter;
                    }
                }
            }
        }
        return smallestQEFCenter;
    }

    private double getQEF(Vector3f center, List<FunctionIntersection> intersections){
        double QEF = 0.0;
        for (int i = 0; i < intersections.size(); i++) {
            FunctionIntersection functionIntersection = intersections.get(i);
            Vector3f pos = functionIntersection.getPosition();
            Vector3f norm = functionIntersection.getNormal();

            double d = -norm.x * pos.x() - norm.y * pos.y() - norm.z * pos.z();
            double denom = norm.x*norm.x + norm.y*norm.y + norm.z*norm.z;
            double nom = (norm.x * center.x + norm.y * center.y + norm.z * center.z + d);

            QEF += (nom*nom) / denom;
        }
        return QEF;
    }

}
