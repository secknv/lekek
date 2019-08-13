package net.sknv.engine.graph;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GraphUtils {

    public static void drawLine(ShaderProgram shaderProgram, Vector4f color, Vector3f i, Vector3f f){

        shaderProgram.setUniform("material", new Material(color, 0.5f));

        //setup vertex positions and buffer
        FloatBuffer posBuff = MemoryUtil.memAllocFloat(6);
        posBuff.put(new float[]{i.x,i.y,i.z,f.x,f.y,f.z}).flip();

        //setup indexes and buffer
        IntBuffer idxBuff = MemoryUtil.memAllocInt(2);
        idxBuff.put(new int[]{0,1}).flip();

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);


        int vboId2 = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId2);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_LINES, 2, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glDeleteBuffers(vboId);
    }

    public static void drawLine(ShaderProgram shaderProgram, Vector3f i, Vector3f f) {
        drawLine(shaderProgram, new Vector4f(1f,1f,1f,1f), i, f);
    }

    public static void drawGrid(ShaderProgram shaderProgram, Vector3f origin, int size){

        shaderProgram.setUniform("material", new Material(new Vector4f(10f, 10f, 10f,0f), 0f));

        //setup vertex positions
        float[] pos = new float[3*4*size];

        Vector3f start = new Vector3f();
        origin.add(-size/2, 0f, -size/2, start);

        for(int i=0; i!=4*size; i++){
            if(i<size){
                pos[3*i] = start.x; pos[3*i+1] = start.y; pos[3*i+2] = start.z;
                start.add(new Vector3f(1,0,0));
            } else if (i<2*size){
                pos[3*i] = start.x; pos[3*i+1] = start.y; pos[3*i+2] = start.z;
                start.add(new Vector3f(0,0,1));
            } else if (i<3*size){
                pos[3*i] = start.x; pos[3*i+1] = start.y; pos[3*i+2] = start.z;
                start.add(new Vector3f(-1,0,0));
            } else {
                pos[3*i] = start.x; pos[3*i+1] = start.y; pos[3*i+2] = start.z;
                start.add(new Vector3f(0,0,-1));
            }
        }

        //buffer vertex positions
        FloatBuffer posBuff = MemoryUtil.memAllocFloat(pos.length);
        posBuff.put(pos).flip();


        //setup indexes array
        int[] idx = new int[4*(size+1)];
        idx[idx.length-1] = 0;
        idx[idx.length-2] = size;
        for (int i=0; i!=(idx.length/2)-1; i++){
            idx[i*2]=i;
            if(i<size+1) idx[i*2+1]=3*size-i;
            else idx[i*2+1]=5*size-i;
        }

        IntBuffer idxBuff = MemoryUtil.memAllocInt(idx.length);
        idxBuff.put(idx).flip();

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int vboId2 = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId2);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_LINES, idx.length, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glDeleteBuffers(vboId);
    }

    public static void drawQuad(ShaderProgram shaderProgram,Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4){

        shaderProgram.setUniform("material", new Material(new Vector4f(0f, 255f, 0f,0.5f), .5f));

        //setup vertex positions and buffer

        FloatBuffer posBuff = MemoryUtil.memAllocFloat(12);
        posBuff.put(new float[]{
                p1.x, p1.y, p1.z,
                p2.x, p2.y, p2.z,
                p3.x, p3.y, p3.z,
                p4.x, p4.y, p4.z,
        }).flip();

        //setup indexes array and buffer
        IntBuffer idxBuff = MemoryUtil.memAllocInt(6);
        idxBuff.put(new int[]{
                0,1,2,
                0,2,3,
        }).flip();

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);


        int vboId2 = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId2);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glDeleteBuffers(vboId);

    }
}
