package net.sknv.engine.entities;

import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.MeshUtils;
import net.sknv.engine.graph.ShaderProgram;
import net.sknv.engine.graph.WebColor;
import net.sknv.engine.physics.colliders.BoundingBox;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.ObjectInputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Collider extends Phantom {

    protected transient BoundingBox boundingBox;
    protected boolean movable;
    protected float mass;
    protected Vector3f velocity;
    protected transient Vector3f forces;

    private WebColor showBB = null;

    public Collider(Mesh mesh) {
        super(mesh);
        boundingBox = new OBB(this);
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

        // add BB specific stuff
        setBoundingBox(new OBB(this));
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
        if (showBB!=null) {
            Mesh aabbMesh = MeshUtils.generateAABB(showBB, boundingBox);
            Mesh obbMesh = MeshUtils.generateOBB(showBB, boundingBox);

            shaderProgram.setUniform("modelViewMatrix", viewMatrix);

            //draw meshes
            shaderProgram.setUniform("material", aabbMesh.getMaterial());
            glBindVertexArray(aabbMesh.getVaoId());
            glDrawElements(GL_LINES, aabbMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

            shaderProgram.setUniform("material", obbMesh.getMaterial());
            glBindVertexArray(obbMesh.getVaoId());
            glDrawElements(GL_LINES, obbMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

            //restore state
            glBindVertexArray(0);
            glBindTexture(GL_TEXTURE_2D, 0);
            showBB = null;
        }
    }
    public void drawBB(WebColor color) {
        this.showBB = color;
    }

    @Override
    public String toString() {
        return "Collider{" +
                "boundingBox=" + boundingBox +
                '}';
    }
}
