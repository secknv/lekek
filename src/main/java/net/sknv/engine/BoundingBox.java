package net.sknv.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BoundingBox {
    Vector3f min, max, rot;
    public Vector3f tmin, tmax, trot;
    float[] triangles;

    public BoundingBox(Vector3f min, Vector3f max) {//AABB
        this.min = min;
        this.max = max;
        this.rot = null;
    }
    public BoundingBox(Vector3f min, Vector3f max , Vector3f rot) {//OBB
        this.min = min;
        this.max = max;
        this.rot = rot;
    }

    public Vector3f getMin() {
        return min;
    }

    public Vector3f getMax() {
        return max;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void transform(Matrix4f modelMatrix) {
        Vector4f min = new Vector4f(this.min.x, this.min.y, this.min.z, 1f);
        Vector4f max = new Vector4f(this.max.x, this.max.y, this.max.z, 1f);

        modelMatrix.transform(min);
        modelMatrix.transform(max);

        tmin = new Vector3f(min.x, min.y, min.z);
        tmax = new Vector3f(max.x, max.y, max.z);
    }
}
