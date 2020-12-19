package net.sknv.engine.physics.colliders;

import net.sknv.engine.GameItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface BoundingBox {
    void transform();
    void translate(Vector3f v);
    void rotate(Quaternionf rot);
    GameItem getGameItem();
    EndPoint getMin();
    EndPoint getMax();
}
