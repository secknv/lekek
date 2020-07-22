package net.sknv.engine.physics.colliders;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface BoundingBox {
    void transform();
    void translate(Vector3f v);
    void rotate(Quaternionf rot);
    EndPoint getMin();
    EndPoint getMax();
}
