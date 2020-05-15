package net.sknv.engine;

import net.sknv.engine.collisions.AABB;
import net.sknv.engine.collisions.BoundingBox;
import net.sknv.engine.graph.Mesh;
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
        setRot(this.rot.x + rot.x, this.rot.y + rot.y, this.rot.z + rot.z);
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
