package net.sknv.engine;

import net.sknv.engine.collisions.EndPoint;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BoundingBox {
    public GameItem gameItem;
    public Vector3f min, max, rot;
    public Vector3f tmin, tmax, trot;
    public EndPoint xMin, xMax, yMin, yMax, zMin, zMax;

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

        this.tmin = min;
        this.tmax = max;
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

        this.tmin = min;
        this.tmax = max;
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

    public void transform(Vector3f position) {
        Vector4f min = new Vector4f(this.min.x, this.min.y, this.min.z, 1f);
        Vector4f max = new Vector4f(this.max.x, this.max.y, this.max.z, 1f);

        System.out.println("----transforming------\nmin " + min.x + "," + min.y + "," + min.z + "  max " + max.x + "," + max.y + "," + max.z);

        //this view matrix ignores rotation so that we always get the same AABB for rotated items (maybe change AABB limits according to rot?...)
        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(position).scale(gameItem.getScale());

        modelViewMatrix.transform(min);
        modelViewMatrix.transform(max);

        Vector3f tmin, tmax;
        tmin = new Vector3f(min.x, min.y, min.z);
        tmax = new Vector3f(max.x, max.y, max.z);

        System.out.println("min " + min.x + "," + min.y + "," + min.z + "  max " + max.x + "," + max.y + "," + max.z);

        this.xMin.position = tmin.x;
        this.xMax.position = tmax.x;
        this.yMin.position = tmin.y;
        this.yMax.position = tmax.y;
        this.zMin.position = tmin.z;
        this.zMax.position = tmax.z;

        this.tmin = tmin;
        this.tmax = tmax;
    }

    public void translate(Vector3f v){
        System.out.println("---translating---\n" + this.toString());
        System.out.println("with vector " + v.x + " " + v.y + " " + v.z);
        this.xMin.position += v.x;
        this.xMax.position += v.x;
        this.yMin.position += v.y;
        this.yMax.position += v.y;
        this.zMin.position += v.z;
        this.zMax.position += v.z;
        System.out.println(this.toString());
        this.tmin = new Vector3f(xMin.position, yMin.position, zMin.position);
        this.tmax = new Vector3f(xMax.position, xMin.position, zMax.position);
    }

    @Override
    public String toString() {
        return "min " + xMin.position + "," + yMin.position + "," + zMin.position + "  max " + xMax.position + "," + yMax.position + "," + zMax.position;
    }
}
