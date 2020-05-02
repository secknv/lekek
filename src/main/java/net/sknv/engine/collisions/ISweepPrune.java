package net.sknv.engine.collisions;

import net.sknv.engine.entities.GameItem;

public interface ISweepPrune {

    void addItem(GameItem gameItem) throws Exception;
    int updateItem(GameItem gameItem);
    void removeItem(GameItem gameItem);
}
