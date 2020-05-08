package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class BoundingBox {
    public GameItem gameItem;
    public Vector3f min, max;

    public BoundingBox(GameItem gameItem, Vector3f min, Vector3f max) {//AABB
        this.gameItem = gameItem;
        this.min = min;
        this.max = max;
    }

    public Vector3f getMin() {
        return min;
    }

    public Vector3f getMax() {
        return max;
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

        System.out.println("min " + min.x + "," + min.y + "," + min.z + "  max " + max.x + "," + max.y + "," + max.z);

        this.min = new Vector3f(min.x, min.y, min.z);
        this.max = new Vector3f(max.x, max.y, max.z);
    }

    /*
    public void translate(Vector3f v){
        System.out.println("---translating---\n" + this.toString());
        System.out.println("with vector " + v.x + " " + v.y + " " + v.z);
        this.xMin.position = this.min.x + v.x;
        this.xMax.position = this.max.x + v.x;
        this.yMin.position = this.min.y + v.y;
        this.yMax.position = this.max.y + v.y;
        this.zMin.position = this.min.z + v.z;
        this.zMax.position = this.max.z + v.z;
        System.out.println(this.toString());
        this.tmin = new Vector3f(xMin.position, yMin.position, zMin.position);
        this.tmax = new Vector3f(xMax.position, yMax.position, zMax.position);
    }
     */

    @Override
    public String toString() {
        return "min " + min.x + "," + min.y + "," + min.z + "  max " + max.x + "," + max.y + "," + max.z;
    }
}
