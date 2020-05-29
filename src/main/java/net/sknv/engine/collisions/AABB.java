package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class AABB implements BoundingBox{

    public GameItem gameItem;
    public EndPoint min, max;

    public AABB(GameItem gameItem, Vector3f min, Vector3f max) {//AABB
        this.gameItem = gameItem;
        this.min = new EndPoint(this, min, true);
        this.max = new EndPoint(this, max, false);
    }
    public EndPoint getMin() {
        return min;
    }

    public EndPoint getMax() {
        return max;
    }

    public void transform() {
        Vector4f min = new Vector4f(this.min.getPosition().x, this.min.getPosition().y, this.min.getPosition().z, 1f);
        Vector4f max = new Vector4f(this.max.getPosition().x, this.max.getPosition().y, this.max.getPosition().z, 1f);

        //this view matrix ignores rotation so that we always get the same AABB for rotated items (maybe change AABB limits according to rot?...)
        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(gameItem.getPos()).scale(gameItem.getScale());

        modelViewMatrix.transform(min);
        modelViewMatrix.transform(max);

        this.min.setPosition(new Vector3f(min.x, min.y, min.z));
        this.max.setPosition(new Vector3f(max.x, max.y, max.z));
    }

    public void translate(Vector3f step){
        this.min.getPosition().add(step);
        this.max.getPosition().add(step);
    }

    @Override
    public void rotate(Quaternionf rot) {
        //calculate new AABB
        ArrayList<Vector3f> vertices = gameItem.getMesh().getVertices();
        ArrayList<Vector3f> tvertices = new ArrayList<>();

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(gameItem.getPos()).scale(gameItem.getScale()).rotateXYZ(gameItem.getRot());

        for (Vector3f v : vertices){
            Vector4f tv = new Vector4f(v.x, v.y, v.z, 1);
            modelViewMatrix.transform(tv);
            tvertices.add(new Vector3f(tv.x, tv.y, tv.z));
        }

        min.setPosition(tvertices.get(0));
        max.setPosition(tvertices.get(7));

        for (Vector3f v : tvertices){
            if (v.x < min.getX()) min.setX(v.x);
            if (v.y < min.getY()) min.setY(v.y);
            if (v.z < min.getZ()) min.setZ(v.z);

            if (v.x > max.getX()) max.setX(v.x);
            if (v.y > max.getY()) max.setY(v.y);
            if (v.z > max.getZ()) max.setZ(v.z);
        }
    }

    public String toString() {
        return "min " + min.getPosition().x + "," + min.getPosition().y + "," + min.getPosition().z +
                "\tmax " + max.getPosition().x + "," + max.getPosition().y + "," + max.getPosition().z;
    }
}
