package net.sknv.engine.graph;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class RayCast {
    private ShaderProgram shader;
    private Vector3f origin;
    private Vector3f direction;

    public RayCast(ShaderProgram shader, Vector3f origin, Vector3f direction) {
        this.shader = shader;
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public void drawNormalisedRay(){
        Vector3f ray = new Vector3f();
        origin.add(direction, ray);
        GraphUtils.drawLine(shader, origin, ray);
    }

    public void drawScaledRay(int k, Vector4f color) {
        Vector3f scaled = new Vector3f();
        origin.add(direction.mul(k, scaled), scaled);
        GraphUtils.drawLine(shader, color, origin, scaled);
    }
}
