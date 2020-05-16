package net.sknv.engine;

import net.sknv.engine.collisions.AABB;
import net.sknv.engine.collisions.BoundingBox;
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

    public GameItem() { //skill que dbz mandou has been officialized
        velocity = new Vector3f(0, 0, 0);
        pos = new Vector3f(0, 0, 0);
        rot = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public GameItem(Mesh mesh) {
        this();
        this.mesh = mesh;
        this.boundingBox = new AABB(this, mesh.getMin(), mesh.getMax());
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
        this.rot.x = x;
        this.rot.y = y;
        this.rot.z = z;
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

        // generate objects axis
        xAxis.rotateY(this.getRot().y).rotateZ(this.getRot().z);
        yAxis.rotateX(this.getRot().x).rotateZ(this.getRot().z);
        zAxis.rotateX(this.getRot().x).rotateY(this.getRot().y);

        // rotate on obj axis
        AxisAngle4f xAA = new AxisAngle4f(rot.x, xAxis);
        AxisAngle4f yAA = new AxisAngle4f(rot.y, yAxis);
        AxisAngle4f zAA = new AxisAngle4f(rot.z, zAxis);

        Quaternionf xq = new Quaternionf(xAA);
        Quaternionf yq = new Quaternionf(yAA);
        Quaternionf zq = new Quaternionf(zAA);

        // get rotation on world axis for setRotation

        Vector3f x_rot = new Vector3f();
        Vector3f y_rot = new Vector3f();
        Vector3f z_rot = new Vector3f();
        //xq.mul(yq).mul(zq);

        Vector3f end_rot = new Vector3f();
        xq.getEulerAnglesXYZ(end_rot);

        setRot(this.getRot().x + end_rot.x, this.getRot().y + end_rot.y, this.getRot().z + end_rot.z);
        this.boundingBox.rotate(rot);
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
