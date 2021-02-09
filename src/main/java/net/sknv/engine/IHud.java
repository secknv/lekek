package net.sknv.engine;

import net.sknv.engine.entities.HudElement;

import java.util.ArrayList;

public interface IHud {

    ArrayList<HudElement> getHudElements();

    default void cleanup() {
        ArrayList<HudElement> HudElements = getHudElements();
        for (HudElement elem : HudElements) {
            if (elem.getMesh()!=null) elem.getMesh().cleanUp();
        }
    }
}
