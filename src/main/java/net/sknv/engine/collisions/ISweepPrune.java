package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Vector3f;

import java.util.Set;

public interface ISweepPrune {

    Set<BoundingBox> addItem(GameItem gameItem) throws Exception;
    Set<BoundingBox> checkStepCollisions(GameItem gameItem, Vector3f nextPos);
    void removeItem(GameItem gameItem);
}
