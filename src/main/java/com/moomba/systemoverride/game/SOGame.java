package com.moomba.systemoverride.game;

import com.moomba.systemoverride.engine.*;
import com.moomba.systemoverride.engine.entities.*;
import com.moomba.systemoverride.engine.entities.components.CameraComponent;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.OctreeComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.entities.systems.CameraMovementSystem;
import com.moomba.systemoverride.engine.generation.FunctionToFloat;
import com.moomba.systemoverride.engine.generation.SimplexNoiseFunction;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class SOGame implements Scene{

    private Engine engine;
    private FunctionToFloat function = new FunctionToFloat(new SimplexNoiseFunction(698423));

    public static void main(String[] args){
        System.out.println("Starting System Override");
        Engine engine = new Engine();
        engine.start(new SOGame());
    }

    @Override
    public void init(Engine engine, AssetLoader loader) {
        this.engine = engine;

        addCamera(0, 2, 20);
        addTerrain(0, 0, 0, 3, 20, 16884);

        engine.addSystem(new CameraMovementSystem());
    }

    private void addTerrain(float posX, float posY, float posZ, int detail, float size, int seed) {
        DualContourer dualContourer = new DualContourer();
        Octree octree = new Octree(size);
        for (int i = 0; i < detail; i++)
            octree.subdivide();
        populateOctree(octree);
        Mesh mesh = dualContourer.contoure(octree);
        Entity entity = new Entity();
        MeshComponent meshComponent = new MeshComponent(mesh);
        entity.addComponent(meshComponent);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(posX, posY, posZ);
        entity.addComponent(transformComponent);
        OctreeComponent octreeComponent = new OctreeComponent(octree);
        entity.addComponent(octreeComponent);
        //mesh.setPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        engine.addEntity(entity);
    }

    private float scale = 0.08f;

    private float getNoise(float x, float y, float z){
        return function.noise(x*scale, y*scale, z*scale);
    }

    private Vector3d getGradient(double x, double y, double z){
        return function.getSourceFunction().normal(x*scale, y*scale, z*scale);
    }

    private void populateOctree(Octree octree) {
        octree.processLeafs(node ->{
            node.setSign(0, getNoise(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(1, getNoise(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(2, getNoise(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(3, getNoise(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(4, getNoise(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));
            node.setSign(5, getNoise(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));
            node.setSign(6, getNoise(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));
            node.setSign(7, getNoise(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));

            if(!nodeExhibitsSignChange(node)) return;

            List<Vector3d> edgeIntersectionsList = new ArrayList<>();
            int[] edges = new int[]{
                    0,2,  1,3,  5,7,  4,6, //y axis
                    2,3,  6,7,  4,5,  0,1, //x axis
                    2,6,  3,7,  1,5,  0,4  //z axis
            };
            for(int i = 0; i < edges.length; i+=2){
                if(signChanges(edges[i], edges[i+1], node)) {
                    Vector3f intersectionf = node.getCorner(edges[i]).lerp(
                            node.getCorner(edges[i + 1]),
                            (float) (Math.abs(node.getSign(edges[i])) /
                                    (Math.abs(node.getSign(edges[i])) + Math.abs(node.getSign(edges[i + 1])))),
                            new Vector3f()
                    );
                    edgeIntersectionsList.add(new Vector3d(intersectionf.x, intersectionf.y, intersectionf.z));
                }
            }
            List<Planed> planes = new ArrayList<>();
            edgeIntersectionsList.forEach(intersectionPoint -> {
                Vector3d normal = getGradient(intersectionPoint.x, intersectionPoint.y, intersectionPoint.z);
                planes.add(new Planed(intersectionPoint, normal));
            });
            Vector3f minimizedQEFPosition = minimizeQEF(node, planes);
            Vector3f normal = calculateNormalFromPlanes(planes);
            double QEF = getQEF(minimizedQEFPosition, planes);
            node.setQEF(QEF);
            node.tag(minimizedQEFPosition, normal);
        });
    }

    private boolean nodeExhibitsSignChange(Octree.Node node) {
        if(node.getSign(0) > 0 &&
                node.getSign(1) > 0 &&
                node.getSign(2) > 0 &&
                node.getSign(3) > 0 &&
                node.getSign(4) > 0 &&
                node.getSign(5) > 0 &&
                node.getSign(6) > 0 &&
                node.getSign(7) > 0) return false;
        if(node.getSign(0) < 0 &&
                node.getSign(1) < 0 &&
                node.getSign(2) < 0 &&
                node.getSign(3) < 0 &&
                node.getSign(4) < 0 &&
                node.getSign(5) < 0 &&
                node.getSign(6) < 0 &&
                node.getSign(7) < 0) return false;
        if(node.getSign(0) == 0 &&
                node.getSign(1) == 0 &&
                node.getSign(2) == 0 &&
                node.getSign(3) == 0 &&
                node.getSign(4) == 0 &&
                node.getSign(5) == 0 &&
                node.getSign(6) == 0 &&
                node.getSign(7) == 0) return false;
        return true;
    }

    private Vector3f calculateNormalFromPlanes(List<Planed> planes) {
        Vector3f normal = new Vector3f(0, 0, 0);
        if(planes.size() == 0) System.out.println("ERROR");
        planes.forEach(plane -> normal.add((float) plane.a, (float) plane.b, (float) plane.c));
        normal.div(planes.size());
        normal.normalize();
        return normal;
    }

    private Vector3f minimizeQEF2(Octree.Node node, List<Planed> planes) {
        Vector3f[] corners = new Vector3f[]{
            node.getCorner(0),
            node.getCorner(1),
            node.getCorner(2),
            node.getCorner(3),
            node.getCorner(4),
            node.getCorner(5),
            node.getCorner(6),
            node.getCorner(7)
        };
        double[] qefs = new double[]{
            getQEF(corners[0], planes),
            getQEF(corners[1], planes),
            getQEF(corners[2], planes),
            getQEF(corners[3], planes),
            getQEF(corners[4], planes),
            getQEF(corners[5], planes),
            getQEF(corners[6], planes),
            getQEF(corners[7], planes)
        };
        double qefv = getQEF(node.getCenter(), planes);
        Vector3f vertex = new Vector3f(node.getCenter());
        float iterations = 10;
        for (int i = 0; i < iterations; i++) {
            for (int i1 = 0; i1 < corners.length; i1++) {
                Vector3f corner = corners[i1];
                double qef = qefs[i1];
                double distance = (Math.abs(qef)+Math.abs(qefv));
                float lerp = (float) (qefv/distance);
                vertex.lerp(corner, lerp*0.5f);
                qefv = getQEF(vertex, planes);
            }
        }
        return vertex;
    }

    private Vector3f minimizeQEF(Octree.Node node, List<Planed> planes) {
        float divisions = 16*(1+(1/node.getDepth()));
        float stepSize = node.getEdgeSize()/divisions;
        double smallestQEF = 999999;
        Vector3f smallestQEFCenter = new Vector3f();
        for(float ix = 0; ix < divisions; ix++){
            for(float iy = 0; iy < divisions; iy++) {
                for (float iz = 0; iz < divisions; iz++) {
                    Vector3f cellCenter = new Vector3f(
                            node.getCenter().x-(node.getEdgeSize()/2)+(ix*stepSize)+(stepSize/2),
                            node.getCenter().y-(node.getEdgeSize()/2)+(iy*stepSize)+(stepSize/2),
                            node.getCenter().z-(node.getEdgeSize()/2)+(iz*stepSize)+(stepSize/2)
                    );
                    double QEF = getQEF(cellCenter, planes);
                    if(QEF < smallestQEF){
                        smallestQEF = QEF;
                        smallestQEFCenter = cellCenter;
                    }
                }
            }
        }
        return smallestQEFCenter;
    }

    private double getQEF(Vector3f center, List<Planed> planes){
        double QEF = 0.0;
        for (int i = 0; i < planes.size(); i++) {
            Planed plane = planes.get(i);
            double denom = plane.a * plane.a + plane.b * plane.b + plane.c * plane.c;
            double nomSqrt = (plane.a * center.x + plane.b * center.y + plane.c * center.z + plane.d);
            double distSq = nomSqrt*nomSqrt / denom;
            QEF += distSq;
        }
        return QEF;
    }
    private boolean signChanges(int a, int b, Octree.Node node) {
        return (node.getSign(a) < 0 && node.getSign(b) > 0) ||
                (node.getSign(b) < 0 && node.getSign(a) > 0) ||
                        (node.getSign(a) == 0 && node.getSign(b) != 0) ||
                        (node.getSign(b) == 0 && node.getSign(a) != 0);
    }

    private void addCube(float x, float y, float z, float size, float r, float g, float b) {
        MeshComponent meshComponent = new MeshComponent(MeshBuilder.cube(size, r, g, b));
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(x, y, z);
        Entity entity = new Entity();
        entity.addComponent(transformComponent);
        entity.addComponent(meshComponent);
        engine.addEntity(entity);
    }

    private void addCamera(float x, float y, float z) {
        TransformComponent transformComponentCam = new TransformComponent();
        transformComponentCam.getPosition().set(x, y, z);
        CameraComponent cameraComponent = new CameraComponent(true, 50, 1000, 600, 0.0001, 1000);
        Entity camera = new Entity();
        camera.addComponent(transformComponentCam);
        camera.addComponent(cameraComponent);
        engine.addEntity(camera);
    }

    private void addAxes(float x, float y, float z, float size) {
        MeshComponent meshComponent = new MeshComponent(MeshBuilder.axes(size));
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(x, y, z);
        Entity entity = new Entity();
        entity.addComponent(transformComponent);
        entity.addComponent(meshComponent);
        engine.addEntity(entity);
    }
}
