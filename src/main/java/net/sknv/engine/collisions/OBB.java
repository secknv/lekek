package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.*;
import org.joml.Math;

public class OBB implements BoundingBox{

    public GameItem gameItem;
    private Vector3f center;
    private Vector3f x, y, z;

    private EndPoint min, max;

    public OBB(GameItem gameItem, Vector3f min, Vector3f max) {
        this.gameItem = gameItem;

        this.min = new EndPoint(this, min, true);
        this.max = new EndPoint(this, max, false);

        this.x = new Vector3f(Math.abs(max.x - min.x)/2,0,0);
        this.y = new Vector3f(0,Math.abs(max.y - min.y)/2,0);
        this.z = new Vector3f(0,0,Math.abs(max.z - min.z)/2);

        this.center = new Vector3f(min.x + x.x, min.y + y.y, min.z + z.z);
        //this.center = new Vector3f(0, 0, 0);
    }

    public void transform() {
        Vector4f tc = new Vector4f(center.x, center.y, center.z, 1);
        Vector4f tx = new Vector4f(x.x, x.y, x.z,1);
        Vector4f ty = new Vector4f(y.x, y.y, y.z,1);
        Vector4f tz = new Vector4f(z.x, z.y, z.z,1);

        //tbr testing purposes
        Vector4f min = new Vector4f(this.min.getPosition().x, this.min.getPosition().y, this.min.getPosition().z, 1f);
        Vector4f max = new Vector4f(this.max.getPosition().x, this.max.getPosition().y, this.max.getPosition().z, 1f);

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(gameItem.getPos()).scale(gameItem.getScale());
        modelViewMatrix.transform(tc);
        //tbr testing purposes
        modelViewMatrix.transform(min);
        modelViewMatrix.transform(max);

        modelViewMatrix.identity().rotateXYZ(gameItem.getRot()).scale(gameItem.getScale());
        modelViewMatrix.transform(tx);
        modelViewMatrix.transform(ty);
        modelViewMatrix.transform(tz);

        this.x = new Vector3f(tx.x, tx.y, tx.z);
        this.y = new Vector3f(ty.x, ty.y, ty.z);
        this.z = new Vector3f(tz.x, tz.y, tz.z);
        this.center = new Vector3f(tc.x, tc.y, tc.z);

        //tbr testing purposes
        this.min.setPosition(new Vector3f(min.x, min.y, min.z));
        this.max.setPosition(new Vector3f(max.x, max.y, max.z));
    }

    @Override
    public void translate(Vector3f step) {
        this.min.getPosition().add(step);
        this.max.getPosition().add(step);
        this.center.add(step);

    }

    public void rotate(Quaternionf rot){
        Vector3f d = new Vector3f();
        center.sub(gameItem.getPos(),d);
        d.rotate(rot);

        System.out.println(d);

        this.center = new Vector3f(gameItem.getPos().x + d.x, gameItem.getPos().y + d.y, gameItem.getPos().z + d.z);

        this.x.rotate(rot);
        this.y.rotate(rot);
        this.z.rotate(rot);
    }

    @Override
    public EndPoint getMin() {
        return min;
    }

    @Override
    public EndPoint getMax() {
        return max;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getX() {
        return x;
    }

    public Vector3f getY() {
        return y;
    }

    public Vector3f getZ() {
        return z;
    }

}
