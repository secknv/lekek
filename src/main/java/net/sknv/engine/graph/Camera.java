package net.sknv.engine.graph;

import org.joml.Vector3f;

public class Camera {

    private final Vector3f pos, rot;

    public Camera() {
        pos = new Vector3f(0, 0, 0);
        rot = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f pos, Vector3f rot) {
        this.pos = pos;
        this.rot = rot;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(float x, float y, float z) {
        pos.x = x;
        pos.y = y;
        pos.z = z;
    }

    public void movePos(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            pos.x += (float) Math.sin(Math.toRadians(rot.y)) * -1.0f * offsetZ;
            pos.z += (float) Math.cos(Math.toRadians(rot.y)) * offsetZ;
        }
        if (offsetX != 0) {
            pos.x += (float)Math.sin(Math.toRadians(rot.y - 90)) * -1.0f * offsetX;
            pos.z += (float)Math.cos(Math.toRadians(rot.y - 90)) * offsetX;
        }
        pos.y += offsetY;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(float x, float y, float z) {
        rot.x = x;
        rot.y = y;
        rot.z = z;
    }

    public void moveRot(float offsetX, float offsetY, float offsetZ) {
        rot.x += offsetX;
        rot.y += offsetY;
        rot.z += offsetZ;
    }
}
