package net.sknv.engine;

import java.util.ArrayList;

public interface IHud {

    ArrayList<GameItem> getGameItems();

    default void cleanup() {
        ArrayList<GameItem> gameItems = getGameItems();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
