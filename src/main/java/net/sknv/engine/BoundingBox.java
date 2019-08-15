package net.sknv.engine;

import org.joml.Vector3f;

public class BoundingBox {
    Vector3f min, max, rot;

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
}
