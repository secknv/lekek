package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class OBB implements BoundingBox{

    public GameItem gameItem;
    private Vector3f center;
    private Vector3f x, y, z;
    private float hx, hy, hz;

    private EndPoint min, max;

    public OBB(GameItem gameItem, Vector3f min, Vector3f max) {
        this.gameItem = gameItem;

        this.x = new Vector3f(1,0,0);
        this.y = new Vector3f(0,1,0);
        this.z = new Vector3f(0,0,1);

        this.hx = Math.abs(max.x - min.x)/2;
        this.hy = Math.abs(max.y - min.y)/2;
        this.hz = Math.abs(max.z - min.z)/2;

        this.center = new Vector3f(min.x + hx, min.y + hy, min.z + hz);

        this.min = new EndPoint(this, min, true);
        this.max = new EndPoint(this, max, false);

        System.out.println(center);
        System.out.println(hx);
        System.out.println(hy);
        System.out.println(hz);
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

        modelViewMatrix.identity().rotateXYZ(gameItem.getRot());
        modelViewMatrix.transform(tx);
        modelViewMatrix.transform(ty);
        modelViewMatrix.transform(tz);

        this.x = new Vector3f(tx.x, tx.y, tx.z);
        this.y = new Vector3f(ty.x, ty.y, ty.z);
        this.z = new Vector3f(tz.x, tz.y, tz.z);
        this.center = new Vector3f(tc.x, tc.y, tc.z);

        this.hx *= gameItem.getScale();
        this.hy *= gameItem.getScale();
        this.hz *= gameItem.getScale();

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

    public void rotate(Vector3f rot){
        Vector4f tx = new Vector4f(x.x, x.y, x.z,1);
        Vector4f ty = new Vector4f(y.x, y.y, y.z,1);
        Vector4f tz = new Vector4f(z.x, z.y, z.z,1);

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().rotateXYZ(rot);
        modelViewMatrix.transform(tx);
        modelViewMatrix.transform(ty);
        modelViewMatrix.transform(tz);

        this.x = new Vector3f(tx.x, tx.y, tx.z);
        this.y = new Vector3f(ty.x, ty.y, ty.z);
        this.z = new Vector3f(tz.x, tz.y, tz.z);
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

    public float getHx() {
        return hx;
    }

    public float getHy() {
        return hy;
    }

    public float getHz() {
        return hz;
    }
}
