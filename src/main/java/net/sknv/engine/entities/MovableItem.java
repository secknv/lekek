package net.sknv.engine.entities;

import net.sknv.engine.BoundingBox;
import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

public class MovableItem extends GameItem{

    protected BoundingBox boundingBox;
    public Vector3f accel;
    public int nCollisions;


    public MovableItem(Mesh mesh) {
        super(mesh);
        accel = new Vector3f();
        boundingBox = new BoundingBox(mesh.getMin(), mesh.getMax());
    }
    public void rotate(float x, float y, float z) {
        setRotation(rotation.x + x, rotation.y + y, rotation.z + z);
    }
    public void move(float x, float y, float z) {
        setPosition(position.x + x, position.y + y, position.z + z);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
