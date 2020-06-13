package net.sknv.engine;

import net.sknv.engine.collisions.BoundingBox;
import net.sknv.engine.collisions.OBB;
import net.sknv.engine.graph.Mesh;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GameItem {

    private Mesh mesh;
    private Vector3f rot;
    private float scale;
    protected Vector3f pos;
    protected BoundingBox boundingBox;
    public Vector3f velocity;
    public int nCollisions;

    public GameItem() {
        velocity = new Vector3f(0, 0, 0);
        pos = new Vector3f(0, 0, 0);
        rot = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public GameItem(Mesh mesh) {
        this();
        this.mesh = mesh;
        this.boundingBox = new OBB(this, mesh.getMin(), mesh.getMax());
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getRot() {
        return rot;
    }

    public float getScale() {
        return scale;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    public void setPos(Vector3f pos){
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        this.pos.z = pos.z;
    }

    public void setRot(Vector3f rot) {
        setRot(rot.x, rot.y, rot.z);
    }

    public void setRot(float x, float y, float z) {
        this.rot.x = (float) (x % (2*Math.PI));
        this.rot.y = (float) (y % (2*Math.PI));
        this.rot.z = (float) (z % (2*Math.PI));
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void rotate(Vector3f rot) {
        // Object POV axis
        Vector3f xAxis = new Vector3f(1,0,0);
        Vector3f yAxis = new Vector3f(0,1,0);
        Vector3f zAxis = new Vector3f(0,0,1);

        //quaternions to get to current rot
        Quaternionf cur = new Quaternionf(new AxisAngle4f(this.getRot().x, xAxis));
        Quaternionf curY = new Quaternionf(new AxisAngle4f(this.getRot().y, yAxis));
        Quaternionf curZ = new Quaternionf(new AxisAngle4f(this.getRot().z, zAxis));
        cur.mul(curY).mul(curZ);

        // generate objects axis
        cur.transform(xAxis);
        cur.transform(yAxis);
        cur.transform(zAxis);

        Quaternionf xq = new Quaternionf(new AxisAngle4f(rot.x, xAxis));
        Quaternionf yq = new Quaternionf(new AxisAngle4f(rot.y, yAxis));
        Quaternionf zq = new Quaternionf(new AxisAngle4f(rot.z, zAxis));

        // get rotation on world axis for setRotation
        xq.mul(yq).mul(zq);

        Quaternionf obbRot = new Quaternionf();
        xq.get(obbRot);

        //combine
        xq.mul(cur);

        Vector3f end_rot = Utils.getEulerAngles(xq);

        setRot(end_rot.x, end_rot.y, end_rot.z);//set item rot
        this.boundingBox.rotate(obbRot);//set bb rot
    }

    @Override
    public String toString() {
        return "GameItem{" +
                "color=" + this.mesh.getMaterial() +
                ", pos=" + pos +
                ", boundingBox=" + boundingBox +
                ", nCollisions=" + nCollisions +
                '}';
    }

    public void translate(Vector3f step) {
        this.pos.x += step.x;
        this.pos.y += step.y;
        this.pos.z += step.z;
        this.boundingBox.translate(step);
    }
}
