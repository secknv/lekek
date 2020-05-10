package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Vector3f;

import java.util.ArrayList;

public interface ISweepPrune {

    void addItem(GameItem gameItem) throws Exception;
    ArrayList<BoundingBox> updateItem(GameItem gameItem, Vector3f nextPos);
    void removeItem(GameItem gameItem);
}
