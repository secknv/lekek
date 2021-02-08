package net.sknv.engine.entities;

import net.sknv.engine.Utils;
import net.sknv.engine.graph.IRenderable;
import net.sknv.engine.graph.ShaderProgram;
import net.sknv.engine.physics.colliders.BoundingBox;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    public void setRotationEuclidean(Vector3f euclideanRot) {
        euclideanRot.sub(rotation);
        rotateEuclidean(euclideanRot);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void rotateEuclidean(Vector3f rot) {
        // Object POV axis
        Vector3f xAxis = new Vector3f(1,0,0);
        Vector3f yAxis = new Vector3f(0,1,0);
        Vector3f zAxis = new Vector3f(0,0,1);

        //quaternions to get to current rot
        Quaternionf current = new Quaternionf(new AxisAngle4f(this.getRotation().x, xAxis));
        Quaternionf curY = new Quaternionf(new AxisAngle4f(this.getRotation().y, yAxis));
        Quaternionf curZ = new Quaternionf(new AxisAngle4f(this.getRotation().z, zAxis));
        current.mul(curY).mul(curZ);

        // generate rotated object axis'
        current.transform(xAxis);
        current.transform(yAxis);
        current.transform(zAxis);

        Quaternionf xq = new Quaternionf(new AxisAngle4f(rot.x, xAxis));
        Quaternionf yq = new Quaternionf(new AxisAngle4f(rot.y, yAxis));
        Quaternionf zq = new Quaternionf(new AxisAngle4f(rot.z, zAxis));

        // get rotation on world axis for setRotation
        xq.mul(yq).mul(zq);

        Quaternionf rotQuaternion = new Quaternionf();
        xq.get(rotQuaternion);

        //combine
        xq.mul(current);

        rotation = Utils.getEulerAngles(xq);//set item rot
        this.boundingBox.rotate(rotQuaternion);//set bb rot
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
}
