package net.sknv.engine.graph;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private static final Vector3f DEFAULT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    public final int vaoId;

    public final List<Integer> vboIdList;

    public final int vertexCount;

    private Texture texture;

    private Vector3f color;

    public Mesh(float[] pos, float[] textCoords, float[] normals, int[] idx) {
        FloatBuffer posbuff = null;
        FloatBuffer textCoordsBuff = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer idxbuff = null;

        try {
            color = DEFAULT_COLOR;
            vertexCount = idx.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO #0
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posbuff = MemoryUtil.memAllocFloat(pos.length);
            posbuff.put(pos).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posbuff, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // texture coords VBO #1
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuff = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuff.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuff, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO #2
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO #2
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            idxbuff = MemoryUtil.memAllocInt(idx.length);
            idxbuff.put(idx).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxbuff, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);


        }
        finally {
            if (posbuff != null) {
                MemoryUtil.memFree(posbuff);
            }
            if (textCoordsBuff != null) {
                MemoryUtil.memFree(textCoordsBuff);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (idxbuff != null) {
                MemoryUtil.memFree(idxbuff);
            }
        }
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
        if (texture != null) {
            //tell openGL to use first texture bank and bind texture
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId());
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        else {
            //for test models
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        }

        //draw mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        //restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        //delete VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) glDeleteBuffers(vboId);

        //delete texture
        if (texture != null) {
            texture.cleanup();
        }

        //delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
