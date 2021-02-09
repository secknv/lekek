package net.sknv.engine.physics.collisionDetection;

import net.sknv.engine.entities.GameItemMesh;
import net.sknv.engine.physics.colliders.BoundingBox;
import org.joml.Vector3f;

import java.util.Set;

public interface ISweepPrune {

    /**
     * @param gameItem the {@link GameItemMesh} to be added to the Sweep & Prune algorithm.
     * @return a set containing the bounding boxes that collide with the added {@link GameItemMesh}.
     */
    Set<BoundingBox> addItem(GameItemMesh gameItem);

    /**
     * Given the provided step, checks if the provided {@link GameItemMesh} collides with any bounding boxes added to the
     * Sweep & Prune algorithm via {@link #addItem(GameItemMesh)} and returns those bounding boxes.
     * @param gameItem the {@link GameItemMesh} to be checked.
     * @param step the step to be performed by the {@link GameItemMesh}.
     * @return a set of {@link BoundingBox} containing the bounding boxes colliding with the tested step.
     */
    Set<BoundingBox> checkStepCollisions(GameItemMesh gameItem, Vector3f step);

    /**
     * Removes the provided {@link GameItemMesh} from the Sweep & Prune collision detection algorithm.
     * @param gameItem the {@link GameItemMesh} to be removed from the Sweep & Prune algorithm.
     */
    void removeItem(GameItemMesh gameItem);
}
