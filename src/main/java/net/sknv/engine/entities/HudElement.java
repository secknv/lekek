package net.sknv.engine.entities;

import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.ShaderProgram;
import net.sknv.engine.graph.Transformation;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class HudElement extends GameItemMesh {

    protected transient Mesh mesh;

    public HudElement() {
        super();
    }

    public HudElement(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    @Override
    public void render(ShaderProgram shaderProgram, Matrix4f orthoProjMatrix) {
        shaderProgram.bind();

        int drawMode = GL_TRIANGLES;
        // Set ortohtaphic and model matrix for this HUD item
        Matrix4f projModelMatrix = Transformation.getOrtoProjModelMatrix(this, orthoProjMatrix);

        shaderProgram.setUniform("projModelMatrix", projModelMatrix);
        shaderProgram.setUniform("colour", mesh.getMaterial().getAmbientColor());
        shaderProgram.setUniform("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);

        shaderProgram.unbind();
    }
}
