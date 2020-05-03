package net.sknv.engine.entities;

import net.sknv.engine.BoundingBox;
import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

public class GameItem implements IEntity{

    private Mesh mesh;

    private final Vector3f pos;
    private final Vector3f rot;

    private float scale;

    private BoundingBox boundingBox;

    public int nCollisions;

    public GameItem() {
        pos = new Vector3f(0, 0, 0);
        rot = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public GameItem(Mesh mesh) {
        this();
        this.mesh = mesh;

        boundingBox = new BoundingBox(this, mesh.min, mesh.max);
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

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        boundingBox.transform(this);
    }

    public void setRot(float x, float y, float z) {
        this.rot.x = x;
        this.rot.y = y;
        this.rot.z = z;
        boundingBox.transform(this);
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
}
