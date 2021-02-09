package net.sknv.engine.entities;

import net.sknv.engine.graph.IRenderable;
import net.sknv.engine.graph.ShaderProgram;
import net.sknv.engine.physics.colliders.BoundingBox;
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

    // todo: move this to Collider type?
    protected transient BoundingBox boundingBox;
    protected boolean movable;
    protected float mass;
    protected Vector3f velocity;
    protected transient Vector3f forces;

    public AbstractGameItem() {
        velocity = new Vector3f(0, 0, 0);
        forces = new Vector3f(0, 0, 0);
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        movable = false;
        mass = 1;
        scale = 1;
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

    public Vector3f getVelocity() {
        return velocity;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
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


    public void translate(Vector3f step) {
        this.position.x += step.x;
        this.position.y += step.y;
        this.position.z += step.z;
        this.boundingBox.translate(step);
    }

    public boolean isMovable() {
        return movable;
    }

    public float getMass() {
        return mass;
    }

    public void applyForce(Vector3f force) {
        forces.add(force);
    }

    @Override
    public String toString() {
        return "AbstractGameItem{" +
                "Type=" + (this instanceof HudElement) +
                '}';
    }
}
