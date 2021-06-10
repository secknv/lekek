package net.sknv.engine.entities;

import net.sknv.engine.graph.*;
import net.sknv.engine.physics.colliders.BoundingBox;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Collider extends Phantom {

    protected transient BoundingBox boundingBox;
    protected boolean movable;
    protected float mass;
    protected Vector3f velocity;
    protected transient Vector3f forces;

    private boolean showBB = false;
    private transient Mesh bbMesh;

    public Collider(Mesh mesh) {
        super(mesh);
        boundingBox = new OBB(this);
        bbMesh = MeshUtils.generateBB(WebColor.Purple, boundingBox);

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

    @Override
    public void render(ShaderProgram shaderProgram, Matrix4f viewMatrix) {
        super.render(shaderProgram, viewMatrix);
        if (showBB) {
            Matrix4f transformationResult = Transformation.getModelViewMatrix(this, viewMatrix);

            shaderProgram.setUniform("modelViewMatrix", transformationResult);
            shaderProgram.setUniform("material", bbMesh.getMaterial());

            //draw mesh
            glBindVertexArray(bbMesh.getVaoId());

            glDrawElements(GL_LINES, bbMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

            //restore state
            glBindVertexArray(0);
            glBindTexture(GL_TEXTURE_2D, 0);
            showBB = false;
        }
    }
    public void drawBB() {
        this.showBB = true;
    }
}
