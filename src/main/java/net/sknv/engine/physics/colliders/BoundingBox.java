package net.sknv.engine.physics.colliders;

import net.sknv.engine.entities.Collider;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface BoundingBox {
    void translate(Vector3f v);
    void rotate(Quaternionf rot);
    void setRotation(Quaternionf rot);
    Collider getCollider();
    EndPoint getMin();
    EndPoint getMax();
}
