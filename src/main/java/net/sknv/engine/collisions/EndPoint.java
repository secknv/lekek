package net.sknv.engine.collisions;

import net.sknv.engine.BoundingBox;

public class EndPoint {
    private BoundingBox box; //AABB
    public float position;
    public boolean isMin;

    public EndPoint(BoundingBox box, float position, boolean isMin) {
        this.box = box;
        this.position = position;
        this.isMin = isMin;
    }

    @Override
    public String toString() {
        String min = isMin ? "min" : "max";
        return  position + " " + min;
    }

    public BoundingBox getBox() {
        return box;
    }
}
