package com.moomba.systemoverride.engine.generation;

import org.joml.*;

import java.util.List;

@SuppressWarnings("Duplicates")
public class QEFMinimizer {

    public void minimizeQEFsForTaggedNodes(Octree.Node root, Function function){
        root.processLeafs(node -> {
            Vector3f minimizer = minimizeQEF(node);
            Vector3d normald = function.normal(minimizer.x, minimizer.y, minimizer.z);
            Vector3f normal = new Vector3f((float) normald.x, (float) normald.y, (float) normald.z);
            FunctionIntersection functionIntersection = new FunctionIntersection(minimizer, normal);
            node.setQEFMinimizer(functionIntersection);
        });
    }

    private Vector3f minimizeQEF(Octree.Node node) {
        float divisions = 32;
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
