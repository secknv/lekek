package net.sknv.engine;

import net.sknv.engine.graph.DirectionalLight;
import org.joml.Vector3f;

import java.io.Serializable;

public class SceneLight implements Serializable {

    private Vector3f ambientLight;

    private DirectionalLight directionalLight;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

}
