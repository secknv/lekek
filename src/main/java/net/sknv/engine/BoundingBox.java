package net.sknv.engine;

import net.sknv.engine.collisions.EndPoint;
import net.sknv.engine.graph.Transformation;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BoundingBox {
    public GameItem gameItem;
    public Vector3f min, max, rot;
    public Vector3f tmin, tmax, trot;
    public EndPoint xMin, xMax, yMin, yMax, zMin, zMax;
    private float[] triangles; //...
    private Transformation transformation = new Transformation();

    public BoundingBox(GameItem gameItem, Vector3f min, Vector3f max) {//AABB
        this.gameItem = gameItem;
        this.min = min;
        this.max = max;
        this.rot = null;
        this.xMin = new EndPoint(this, min.x, true);
        this.xMax = new EndPoint(this, max.x, false);
        this.yMin = new EndPoint(this, min.y, true);
        this.yMax = new EndPoint(this, max.y, false);
        this.zMin = new EndPoint(this, min.z, true);
        this.zMax = new EndPoint(this, max.z, false);
    }
    public BoundingBox(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
        this.rot = null;
        this.xMin = new EndPoint(this, min.x, true);
        this.xMax = new EndPoint(this, max.x, false);
        this.yMin = new EndPoint(this, min.y, true);
        this.yMax = new EndPoint(this, max.y, false);
        this.zMin = new EndPoint(this, min.z, true);
        this.zMax = new EndPoint(this, max.z, false);
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

    public void transform(GameItem gameItem) {
        Vector4f min = new Vector4f(this.min.x, this.min.y, this.min.z, 1f);
        Vector4f max = new Vector4f(this.max.x, this.max.y, this.max.z, 1f);

        transformation.getModelMatrix(gameItem).transform(min);
        transformation.getModelMatrix(gameItem).transform(max);

        tmin = new Vector3f(min.x, min.y, min.z);
        tmax = new Vector3f(max.x, max.y, max.z);

        this.xMin.position = tmin.x;
        this.xMax.position = tmax.x;
        this.yMin.position = tmin.y;
        this.yMax.position = tmax.y;
        this.zMin.position = tmin.z;
        this.zMax.position = tmax.z;
    }
}
