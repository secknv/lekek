package net.sknv.engine.collisions;

import net.sknv.engine.GameItem;
import org.joml.Vector3f;

import java.util.Set;

public interface ISweepPrune {

    /**
     * @param gameItem the {@link GameItem} to be added to the Sweep & Prune algorithm.
     * @return a set containing the bounding boxes that collide with the added {@link GameItem}.
     */
    Set<BoundingBox> addItem(GameItem gameItem);

    /**
     * Given the provided step, checks if the provided {@link GameItem} collides with any bounding boxes added to the 
     * Sweep & Prune algorithm via {@link #addItem(GameItem)} and returns those bounding boxes.
     * @param gameItem the {@link GameItem} to be checked.
     * @param step the step to be performed by the {@link GameItem}.
     * @return a set of {@link BoundingBox} containing the bounding boxes colliding with the tested step.
     */
    Set<BoundingBox> checkStepCollisions(GameItem gameItem, Vector3f step);

    /**
     * Removes the provided {@link GameItem} from the Sweep & Prune collision detection algorithm.
     * @param gameItem the {@link GameItem} to be removed from the Sweep & Prune algorithm.
     */
    void removeItem(GameItem gameItem);
}
