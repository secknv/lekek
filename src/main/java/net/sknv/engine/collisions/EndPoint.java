package net.sknv.engine.collisions;

import org.joml.Vector3f;

public class EndPoint {
    private BoundingBox boundingBox; //AABB
    private Vector3f position;
    private boolean isMin;

    public EndPoint(BoundingBox box, Vector3f position, boolean isMin) {
        this.boundingBox = box;
        this.position = position;
        this.isMin = isMin;
    }

    @Override
    public String toString() {
        String min = isMin ? "min" : "max";
        return  position + " " + min;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public boolean isMin() {
        return isMin;
    }

    public BoundingBox getBB() {
        return boundingBox;
    }

}
