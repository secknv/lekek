package net.sknv.game;

import net.sknv.engine.entities.GameItem;
import net.sknv.engine.entities.MovableItem;
import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

public class Boid extends MovableItem {

    public Boid(Mesh mesh) {
        super(mesh);
        accel = new Vector3f( 0, 0, 0);
    }

}
