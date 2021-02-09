package net.sknv.engine.physics.collisionDetection;

import net.sknv.engine.entities.Collider;
import net.sknv.engine.entities.GameItemMesh;
import net.sknv.engine.physics.colliders.BoundingBox;
import org.joml.Vector3f;

import java.util.Set;

public interface ISweepPrune {

    /**
     * @param collider the {@link Collider} to be added to the Sweep & Prune algorithm.
     * @return a set containing the bounding boxes that collide with the added {@link GameItemMesh}.
     */
    Set<BoundingBox> addItem(Collider collider);

    /**
     * Given the provided step, checks if the provided {@link Collider} collides with any bounding boxes added to the
     * Sweep & Prune algorithm via {@link #addItem(Collider)} and returns those bounding boxes.
     * @param collider the {@link Collider} to be checked.
     * @param step the step to be performed by the {@link Collider}.
     * @return a set of {@link BoundingBox} containing the bounding boxes colliding with the tested step.
     */
    Set<BoundingBox> checkStepCollisions(Collider collider, Vector3f step);

    /**
     * Removes the provided {@link Collider} from the Sweep & Prune collision detection algorithm.
     * @param collider the {@link Collider} to be removed from the Sweep & Prune algorithm.
     */
    void removeItem(Collider collider);
}
