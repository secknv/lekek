package net.sknv.engine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public final int vaoId;

    public final int posVboId, colorVboId, idxVboId;

    public final int vertexCount;

    public Mesh(float[] pos, float[] colors, int[] idx) {
        FloatBuffer posbuff = null;
        FloatBuffer colorbuff = null;
        IntBuffer idxbuff = null;

        try {
            vertexCount = idx.length;

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            posVboId = glGenBuffers();
            posbuff = MemoryUtil.memAllocFloat(pos.length);
            posbuff.put(pos).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, posbuff, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Colour VBO
            colorVboId = glGenBuffers();
            colorbuff = MemoryUtil.memAllocFloat(colors.length);
            colorbuff.put(colors).flip();
            glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
            glBufferData(GL_ARRAY_BUFFER, colorbuff, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            idxVboId = glGenBuffers();
            idxbuff = MemoryUtil.memAllocInt(idx.length);
            idxbuff.put(idx).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxbuff, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally {
            if (posbuff != null) {
                MemoryUtil.memFree(posbuff);
            }
            if (colorbuff != null) {
                MemoryUtil.memFree(colorbuff);
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
        glDeleteBuffers(posVboId);
        glDeleteBuffers(colorVboId);
        glDeleteBuffers(idxVboId);

        //delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
