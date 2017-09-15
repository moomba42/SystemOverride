package com.moomba.systemoverride.game;

import com.moomba.systemoverride.engine.*;
import com.moomba.systemoverride.engine.entities.*;
import com.moomba.systemoverride.engine.entities.components.CameraComponent;
import com.moomba.systemoverride.engine.entities.components.MeshComponent;
import com.moomba.systemoverride.engine.entities.components.OctreeComponent;
import com.moomba.systemoverride.engine.entities.components.TransformComponent;
import com.moomba.systemoverride.engine.entities.systems.CameraMovementSystem;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class SOGame implements Scene{

    private Engine engine;

    public static void main(String[] args){
        System.out.println("Starting System Override");
        Engine engine = new Engine();
        engine.start(new SOGame());
    }

    @Override
    public void init(Engine engine, AssetLoader loader) {
        this.engine = engine;

        //addAxes(0, 0, 0, 3);
        addCamera(2, 2, 2);
        //addCube(0, 0, 0, 1, 1, 0, 1);
        addTerrain(0, 0, 0, 10, 16884);

        engine.addSystem(new CameraMovementSystem());
    }

    private void addTerrain(float posX, float posY, float posZ, float size, int seed) {
        SimplexNoiseGenerator generator = new SimplexNoiseGenerator(seed);
        DualContourer dualContourer = new DualContourer();
        Octree octree = new Octree(size);
        octree.subdivide();
        octree.subdivide();
        octree.subdivide();
        octree.subdivide();
        populateOctree(octree);
        Mesh mesh = dualContourer.contoure(octree);
        //mesh.setDrawMode(GL_LINES);
        MeshComponent meshComponent = new MeshComponent(mesh);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.getPosition().set(posX, posY, posZ);
        OctreeComponent octreeComponent = new OctreeComponent(octree);
        Entity entity = new Entity();
        entity.addComponent(meshComponent);
        entity.addComponent(transformComponent);
        entity.addComponent(octreeComponent);
        engine.addEntity(entity);
    }

    private void populateOctree(Octree octree) {
        //Noise noise = new PerlinNoise(321);
        Noise noise = new CircleNoise(0, 0, 0, 3.15f);
        octree.processLeafs(node ->{
            node.setSign(0, noise.noisef(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(1, noise.noisef(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(2, noise.noisef(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(3, noise.noisef(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z - node.getEdgeSize()/2));
            node.setSign(4, noise.noisef(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));
            node.setSign(5, noise.noisef(
                    node.getCenter().x + node.getEdgeSize()/2,
                    node.getCenter().y - node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));
            node.setSign(6, noise.noisef(
                    node.getCenter().x - node.getEdgeSize()/2,
                    node.getCenter().y + node.getEdgeSize()/2,
                    node.getCenter().z + node.getEdgeSize()/2));
            node.setSign(7, noise.noisef(
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
                Vector3d normal = noise.gradient(intersectionPoint.x, intersectionPoint.y, intersectionPoint.z);
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
        if(node.getSign(0) >= 0 &&
                node.getSign(1) >= 0 &&
                node.getSign(2) >= 0 &&
                node.getSign(3) >= 0 &&
                node.getSign(4) >= 0 &&
                node.getSign(5) >= 0 &&
                node.getSign(6) >= 0 &&
                node.getSign(7) >= 0) return false;
        if(node.getSign(0) <= 0 &&
                node.getSign(1) <= 0 &&
                node.getSign(2) <= 0 &&
                node.getSign(3) <= 0 &&
                node.getSign(4) <= 0 &&
                node.getSign(5) <= 0 &&
                node.getSign(6) <= 0 &&
                node.getSign(7) <= 0) return false;
        return true;
    }

    private Vector3f calculateNormalFromPlanes(List<Planed> planes) {
        Vector3f normal = new Vector3f(0, 0, 0);
        planes.forEach(plane -> normal.add((float) plane.a, (float) plane.b, (float) plane.c));
        normal.div(planes.size());
        normal.normalize();
        return normal;
    }

    private Vector3f minimizeQEF(Octree.Node node, List<Planed> planes) {
        float divisions = 32;
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
        return (node.getSign(a) < 0 && node.getSign(b) > 0) || (node.getSign(b) < 0 && node.getSign(a) > 0);
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
