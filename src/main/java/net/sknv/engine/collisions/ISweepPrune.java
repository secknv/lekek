package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Vector3f;

public interface ISweepPrune {

    void addItem(GameItem gameItem) throws Exception;
    int updateItem(GameItem gameItem, Vector3f nextPos);
    void removeItem(GameItem gameItem);
}
