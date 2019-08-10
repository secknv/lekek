package net.sknv.engine.graph;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GraphUtils {

    public static void drawLine(Vector3f i, Vector3f f){

        FloatBuffer posBuff = null;
        posBuff = MemoryUtil.memAllocFloat(6);
        posBuff.put(new float[]{i.x, i.y, i.z, f.x, f.y, f.z}).flip();

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindVertexArray(0);

        int vboId2 = glGenBuffers();
        IntBuffer idxBuff = MemoryUtil.memAllocInt(2);
        idxBuff.put(new int[]{0,1}).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId2);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuff, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);

        //loop
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId2);

        glDrawElements(GL_LINES, 2, GL_UNSIGNED_BYTE, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glDeleteBuffers(vboId);
        glDeleteBuffers(vboId2);
        glDeleteVertexArrays(vaoId);

    }
}
