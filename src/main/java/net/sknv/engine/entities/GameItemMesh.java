package net.sknv.engine.entities;

import net.sknv.engine.graph.*;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GameItemMesh extends AbstractGameItem {

    protected transient Mesh mesh;

    public GameItemMesh() {
        super();
    }

    public GameItemMesh(Mesh mesh) {
        this();
        this.mesh = mesh;
        this.boundingBox = new OBB(this);
    }

    @Override
    public void render(ShaderProgram shaderProgram, Matrix4f viewMatrix) {
        int drawMode = GL_TRIANGLES;

        Matrix4f transformationResult = Transformation.getModelViewMatrix(this, viewMatrix);

        shaderProgram.setUniform("modelViewMatrix", transformationResult);
        shaderProgram.setUniform("material", mesh.getMaterial());

        // this part used to be on Mesh::render
        Texture texture = mesh.getMaterial().getTexture();
        if (texture != null) {
            //tell openGL to use first texture bank and bind texture
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        //draw mesh
        glBindVertexArray(mesh.getVaoId());

        glDrawElements(drawMode, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        //restore state
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public String toString() {
        return "GameItem{" +
                "color=" + this.mesh.getMaterial() +
                ", pos=" + position +
                ", boundingBox=" + boundingBox +
                '}';
    }

    private void readObject(java.io.ObjectInputStream inputStream) throws Exception {
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
}
