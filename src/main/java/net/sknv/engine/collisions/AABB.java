package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    }

    public String toString() {
        return "min " + min.getPosition().x + "," + min.getPosition().y + "," + min.getPosition().z +
                "\tmax " + max.getPosition().x + "," + max.getPosition().y + "," + max.getPosition().z;
    }
}
