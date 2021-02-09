package net.sknv.engine.entities;

import net.sknv.engine.graph.Material;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.OBJLoader;
import net.sknv.engine.physics.colliders.BoundingBox;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Collider extends GameItemMesh{

    protected transient BoundingBox boundingBox;
    protected boolean movable;
    protected float mass;
    protected Vector3f velocity;
    protected transient Vector3f forces;

    public Collider(Mesh mesh) {
        super(mesh);
        this.boundingBox = new OBB(this);

        velocity = new Vector3f(0, 0, 0);
        forces = new Vector3f(0, 0, 0);
        movable = false;
        mass = 1;
    }

    @Override
    public Quaternionf rotateEuclidean(Vector3f rot) {
        Quaternionf rotQuaternion = super.rotateEuclidean(rot);
        this.boundingBox.rotate(rotQuaternion);//set bb rot
        return rotQuaternion;
    }

    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        Mesh mesh = OBJLoader.loadMesh((String) inputStream.readObject());
        mesh.setMaterial((Material) inputStream.readObject());

        setMesh(mesh);
        setBoundingBox(new OBB(this));
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();
        outputStream.writeObject(mesh.getModelFile());
        outputStream.writeObject(mesh.getMaterial());
    }

    @Override
    public void translate(Vector3f step) {
        super.translate(step);
        this.boundingBox.translate(step);
    }

    public void applyForce(Vector3f force) {
        forces.add(force);
    }
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
    public Vector3f getVelocity() {
        return velocity;
    }
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
    public boolean isMovable() {
        return movable;
    }
    public float getMass() {
        return mass;
    }
}
