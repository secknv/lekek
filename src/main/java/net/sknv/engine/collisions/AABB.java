package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Vector3f;

public class AABB extends BoundingBox{

    public AABB(GameItem gameItem, Vector3f min, Vector3f max) {
        super(gameItem, min, max);
    }
}
