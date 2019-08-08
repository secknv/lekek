package net.sknv.engine;

import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

public class GameItem {

    private final Mesh mesh;

    private final Vector3f pos, rot;
    private float scale;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        pos = new Vector3f(0, 0, 0);
        rot = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rot.x = x;
        this.rot.y = y;
        this.rot.z = z;
    }

    public void setScale(float scale) {
        this.scale = scale;
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
}
