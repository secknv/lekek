package net.sknv.engine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public final int vaoId;

    public final List<Integer> vboIdList;

    public final int vertexCount;

    private final Texture texture;

    public Mesh(float[] pos, float[] textCoords, int[] idx, Texture texture) {
        FloatBuffer posbuff = null;
        FloatBuffer textCoordsBuff = null;
        IntBuffer idxbuff = null;

        try {
            this.texture = texture;
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

            // texture VBO #1
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuff = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuff.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuff, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

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
            if (idxbuff != null) {
                MemoryUtil.memFree(idxbuff);
            }
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
        //tell openGL to use first texture bank and bind texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getId());

        //draw mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        //restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        //delete VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) glDeleteBuffers(vboId);

        //delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
