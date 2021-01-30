package net.sknv.engine.physics.colliders;

import net.sknv.engine.GameItem;
import org.joml.Math;
import org.joml.*;

public class OBB extends AABB implements BoundingBox {

    private Vector3f center;
    private Vector3f x, y, z;

    public OBB(GameItem gameItem) {
        super(gameItem);
        Vector3f min = gameItem.getMesh().getMin();
        Vector3f max = gameItem.getMesh().getMax();
        this.x = new Vector3f(Math.abs(max.x - min.x)/2,0,0);
        this.y = new Vector3f(0,Math.abs(max.y - min.y)/2,0);
        this.z = new Vector3f(0,0,Math.abs(max.z - min.z)/2);

        this.center = new Vector3f(min.x + x.x, min.y + y.y, min.z + z.z);

        transform();
    }

    @Override
    public void transform() {
        Vector4f tc = new Vector4f(center.x, center.y, center.z, 1);
        Vector4f tx = new Vector4f(x.x, x.y, x.z,1);
        Vector4f ty = new Vector4f(y.x, y.y, y.z,1);
        Vector4f tz = new Vector4f(z.x, z.y, z.z,1);

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(gameItem.getPos()).scale(gameItem.getScale()).rotateXYZ(gameItem.getRot());
        modelViewMatrix.transform(tc);

        modelViewMatrix.identity().rotateXYZ(gameItem.getRot()).scale(gameItem.getScale());
        modelViewMatrix.transform(tx);
        modelViewMatrix.transform(ty);
        modelViewMatrix.transform(tz);

        this.x = new Vector3f(tx.x, tx.y, tx.z);
        this.y = new Vector3f(ty.x, ty.y, ty.z);
        this.z = new Vector3f(tz.x, tz.y, tz.z);
        this.center = new Vector3f(tc.x, tc.y, tc.z);

        /*
        Vector3f d = new Vector3f();
        center.sub(gameItem.getPos(),d);
        d.rotate(rot);

        this.center = new Vector3f(gameItem.getPos().x + d.x, gameItem.getPos().y + d.y, gameItem.getPos().z + d.z);

        this.x.rotate(rot);
        this.y.rotate(rot);
        this.z.rotate(rot);
         */
    }

    @Override
    public void translate(Vector3f step) {
        super.translate(step);
        this.center.add(step);
    }

    @Override
    public void rotate(Quaternionf rot){
        super.rotate(rot);
        Vector3f d = new Vector3f();
        center.sub(gameItem.getPos(),d);
        d.rotate(rot);

        this.center = new Vector3f(gameItem.getPos().x + d.x, gameItem.getPos().y + d.y, gameItem.getPos().z + d.z);

        this.x.rotate(rot);
        this.y.rotate(rot);
        this.z.rotate(rot);
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
