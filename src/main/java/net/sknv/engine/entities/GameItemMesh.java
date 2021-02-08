package net.sknv.engine.entities;

import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.ShaderProgram;
import net.sknv.engine.graph.Texture;
import net.sknv.engine.graph.Transformation;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GameItemMesh extends AbstractGameItem {

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
}
