package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

public class Boid extends GameItem {

    public Boid(Mesh mesh) {
        super(mesh);
        velocity = new Vector3f( 0, 0, 0);
    }

}
