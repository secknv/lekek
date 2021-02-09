package net.sknv.engine.physics.colliders;

import net.sknv.engine.entities.GameItemMesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface BoundingBox {
    void translate(Vector3f v);
    void rotate(Quaternionf rot);
    GameItemMesh getGameItem();
    EndPoint getMin();
    EndPoint getMax();
}
