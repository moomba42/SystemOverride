package com.moomba.systemoverride.engine;

import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.*;

public final class PerlinNoise implements Noise {

    /** The dimension of this noise. */
    private final int dimension;

    /** The PRNG for gradient generation (for now). */
    private final Random rng;

    /** Cache computed gradients. */
    private final Map<Vector3d, Vector3d> gradients = new HashMap<>();

    /** Pre-calculated list of neighboring grid corner. */
    private final List<Vector3d> corners = new ArrayList<>();

    /**
     * Create new Perlin noise.
     * @param seed       PRNG seed
     */
    public PerlinNoise(final int seed) {
        this.rng = new Random(seed);
        this.dimension = 3;
        for (int i = 0; i < 1 << dimension; i++) {
            double[] v = new double[dimension];
            for (int n = 0; n < dimension; n++) {
                if ((i & (1 << n)) != 0) {
                    v[n] = 1;
                } else {
                    v[n] = 0;
                }
            }
            corners.add(new Vector3d(v[0], v[1], v[2]));
        }
    }

    @Override
    public double noise(double x, double y, double z) {
        double sum = 0;
        Vector3d pfloor = new Vector3d(Math.floor(x), Math.floor(y), Math.floor(z));
        for (Vector3d c : corners) {
            Vector3d q = pfloor.add(c);
            Vector3d g = gradient(x, y, z);
            double m = g.dot(new Vector3d(x, y, z).sub(q));
            Vector3d t = new Vector3d(x, y, z).sub(q).absolute().mul(-1).add(1, 1, 1);
            Vector3d w = new Vector3d(t).mul(t).mul(3).sub(new Vector3d(t.x*t.x*t.x, t.y*t.y*t.y, t.z*t.z*t.z).mul(2));
            sum += 1 * w.x * w.y * w.z * m;
        }
        return sum;
    }

    @Override
    public float noisef(float x, float y, float z) {
        return (float) noise(x, y, z);
    }

    @Override
    public Vector3d gradient(double x, double y, double z) {
        Vector3d gradient = gradients.get(new Vector3d(x, y, z));
        if (gradient == null) {
            Vector3d vector = new Vector3d(rng.nextDouble() - 0.5, rng.nextDouble() - 0.5, rng.nextDouble() - 0.5);
            gradient = new Vector3d(vector).normalize();
            gradients.put(new Vector3d(x, y, z), gradient);
        }
        return gradient;
    }

    @Override
    public Vector3f gradientf(float x, float y, float z) {
        Vector3d gradient = gradient(x, y, z);
        return new Vector3f((float) gradient.x, (float) gradient.y, (float) gradient.z);
    }
}