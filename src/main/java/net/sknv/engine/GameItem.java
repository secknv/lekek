package net.sknv.engine;

import net.sknv.engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GameItem {

    public Vector3f accel;
    public boolean isColliding = false;
    private Mesh mesh;
    private BoundingBox boundingBox;
    private Vector3f pos, nextPos, rot;
    private float scale;

    public GameItem() { //skill que dbz mandou has been officialized
        accel = new Vector3f(0, 0, 0);
        pos = new Vector3f(0, 0, 0);
        nextPos = new Vector3f(0, 0, 0);
        rot = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public GameItem(Mesh mesh) {
        this();
        this.mesh = mesh;
        this.boundingBox = mesh.getAABB(this);
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        boundingBox.transform(this);
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(float x, float y, float z) {
        this.rot.x = x;
        this.rot.y = y;
        this.rot.z = z;
        boundingBox.transform(this);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    //free service bonus methods for dbz
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Vector3f tryMove(){
        nextPos = pos;
        pos = pos.add(accel.mul(0.1f));
        boundingBox.transform(this);
        return new Vector3f(accel.x, accel.y, accel.z);
    }

    public void move() {
        //pos = nextPos;
        accel.zero();
    }
}
