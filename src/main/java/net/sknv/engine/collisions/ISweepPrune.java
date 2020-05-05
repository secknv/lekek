package net.sknv.engine.collisions;

import net.sknv.engine.entities.MovableItem;
import org.joml.Vector3f;

public interface ISweepPrune {

    void addItem(MovableItem movableItem) throws Exception;
    int updateItem(MovableItem movableItem, Vector3f nextPos);
    void removeItem(MovableItem movableItem);
}