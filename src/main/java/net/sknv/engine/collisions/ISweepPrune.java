package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;

public interface ISweepPrune {

    void addItem(GameItem gameItem) throws Exception;
    boolean updateItem(GameItem gameItem);
    void removeItem(GameItem gameItem);
}
