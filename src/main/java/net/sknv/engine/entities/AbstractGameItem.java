package net.sknv.engine.entities;

import net.sknv.engine.graph.IRenderable;
import net.sknv.engine.graph.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.Serializable;

/**
 * This class aggregates all common GameItem code for the different implementations of {@link IRenderable#render(ShaderProgram, Matrix4f)}.
 */
public abstract class AbstractGameItem implements IRenderable, Serializable {

    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;

    public AbstractGameItem() {

        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public void translate(Vector3f step) {
        this.position.x += step.x;
        this.position.y += step.y;
        this.position.z += step.z;
    }

    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getRotation() {
        return rotation;
    }
    public float getScale() {
        return scale;
    }
    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x,y,z));
    }
    public void setPosition(Vector3f position){
        this.position = position;
    }
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "AbstractGameItem{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                '}';
    }


}
