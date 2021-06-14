package net.sknv.engine.physics.colliders;

import net.sknv.engine.entities.Collider;
import org.joml.Math;
import org.joml.*;

public class OBB extends AABB implements BoundingBox {

    private Vector3f center;
    private Vector3f x, y, z;

    public OBB(Collider collider) {
        super(collider);

        Vector3f min = collider.getMesh().getMin();
        Vector3f max = collider.getMesh().getMax();
        this.x = new Vector3f(Math.abs(max.x - min.x)/2,0,0);
        this.y = new Vector3f(0,Math.abs(max.y - min.y)/2,0);
        this.z = new Vector3f(0,0,Math.abs(max.z - min.z)/2);
        this.center = new Vector3f(min.x + x.x, min.y + y.y, min.z + z.z);

        Vector4f tc = new Vector4f(center.x, center.y, center.z, 1);
        Vector4f tx = new Vector4f(x.x, x.y, x.z,1);
        Vector4f ty = new Vector4f(y.x, y.y, y.z,1);
        Vector4f tz = new Vector4f(z.x, z.y, z.z,1);

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(collider.getPosition()).scale(collider.getScale()).rotate(collider.getRotation());
        modelViewMatrix.transform(tc);

        modelViewMatrix.identity().rotate(collider.getRotation()).scale(collider.getScale());
        modelViewMatrix.transform(tx);
        modelViewMatrix.transform(ty);
        modelViewMatrix.transform(tz);

        this.x = new Vector3f(tx.x, tx.y, tx.z);
        this.y = new Vector3f(ty.x, ty.y, ty.z);
        this.z = new Vector3f(tz.x, tz.y, tz.z);
        this.center = new Vector3f(tc.x, tc.y, tc.z);
    }

    @Override
    public void translate(Vector3f step) {
        super.translate(step);
        this.center.add(step);
    }

    @Override
    public void rotate(Quaternionf rot){
        super.rotate(rot);
        Matrix3f rotAsMat = new Matrix3f();
        rot.get(rotAsMat);

        Vector3f myX = new Vector3f();
        Vector3f myY = new Vector3f();
        Vector3f myZ = new Vector3f();
        Matrix3f rotAsMatrix = new Matrix3f();
        collider.getRotation().get(rotAsMatrix);
        rotAsMatrix.getColumn(0,myX);
        rotAsMatrix.getColumn(1,myY);
        rotAsMatrix.getColumn(2,myZ);

        Vector3f rotation = new Vector3f();
        rot.getEulerAnglesXYZ(rotation);

        Vector3f d = new Vector3f();
        center.sub(collider.getPosition(),d);

        Quaternionf qx = new Quaternionf(new AxisAngle4f(rotation.x, myX));
        Quaternionf qy = new Quaternionf(new AxisAngle4f(rotation.y, myY));
        Quaternionf qz = new Quaternionf(new AxisAngle4f(rotation.z, myZ));

        Quaternionf finalRot = new Quaternionf().mul(qx).mul(qy).mul(qz);

        d.rotate(finalRot);

        collider.getPosition().add(d, center);

        this.x.rotate(finalRot);
        this.y.rotate(finalRot);
        this.z.rotate(finalRot);
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
