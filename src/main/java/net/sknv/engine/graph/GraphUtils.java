package net.sknv.engine.graph;

import net.sknv.engine.collisions.AABB;
import net.sknv.engine.collisions.BoundingBox;
import net.sknv.engine.GameItem;
import net.sknv.engine.collisions.OBB;
import org.joml.Matrix4f;
import net.sknv.game.Renderer;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GraphUtils {

    public static void drawLine(Vector3f i, Vector3f f, Renderer renderer, Vector4f color){

        // create Positions Array
        float[] posArray = new float[]{i.x,i.y,i.z,f.x,f.y,f.z};

        // Create Indices Array
        int[] idxArray = new int[]{0,1};

        // Generate AlienVAO and add it to the render queue
        renderer.addAlienVAO(generateAVAO(posArray, idxArray, color, GL_LINES));
    }

    public static void drawGrid(Renderer renderer, Vector3f origin, int size){
        Vector3f start = new Vector3f();
        origin.add(-size/2f, 0f, -size/2f, start);

        // Create Positions Array
        float[] posArray = new float[3*4*size];
        for(int i=0; i!=4*size; i++){
            if(i<size){
                posArray[3*i] = start.x; posArray[3*i+1] = start.y; posArray[3*i+2] = start.z;
                start.add(new Vector3f(1,0,0));
            } else if (i<2*size){
                posArray[3*i] = start.x; posArray[3*i+1] = start.y; posArray[3*i+2] = start.z;
                start.add(new Vector3f(0,0,1));
            } else if (i<3*size){
                posArray[3*i] = start.x; posArray[3*i+1] = start.y; posArray[3*i+2] = start.z;
                start.add(new Vector3f(-1,0,0));
            } else {
                posArray[3*i] = start.x; posArray[3*i+1] = start.y; posArray[3*i+2] = start.z;
                start.add(new Vector3f(0,0,-1));
            }
        }

        // Create Indices Array
        int[] idxArray = new int[4*(size+1)];
        idxArray[idxArray.length-1] = 0;
        idxArray[idxArray.length-2] = size;
        for (int i=0; i!=(idxArray.length/2)-1; i++){
            idxArray[i*2]=i;
            if(i<size+1) idxArray[i*2+1]=3*size-i;
            else idxArray[i*2+1]=5*size-i;
        }

        renderer.addAlienVAO(generateAVAO(posArray, idxArray, new Vector4f(1, 0, 0, 1), GL_LINES));
    }

    public static void drawQuad(Renderer renderer, Vector4f color,Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4){

        // Create Positions Array
        float[] posArray = new float[]{
                p1.x, p1.y, p1.z,
                p2.x, p2.y, p2.z,
                p3.x, p3.y, p3.z,
                p4.x, p4.y, p4.z,
        };

        // Create Indices Array
        int[] idxArray = new int[]{
                0,1,2,
                0,2,3,
        };
        renderer.addAlienVAO(generateAVAO(posArray, idxArray, color, GL_TRIANGLES));
    }

    public static void drawAABB(Renderer renderer, Vector4f color, BoundingBox bb) {

        Vector3f min = bb.getMin().getPosition();
        Vector3f max = bb.getMax().getPosition();

        // Create Positions Array
        float[] posArray = new float[] {
                min.x, min.y, min.z,
                max.x, min.y, min.z,
                min.x, max.y, min.z,
                min.x, min.y, max.z,

                min.x, max.y, max.z,
                max.x, min.y, max.z,
                max.x, max.y, min.z,
                max.x, max.y, max.z,
        };

        // Create Indices Array
        int[] idxArray = new int[] {
                0,1,
                0,2,
                0,3,

                1,5,
                1,6,

                2,4,
                2,6,

                3,4,
                3,5,

                4,7,
                5,7,
                6,7,
        };

        renderer.addAlienVAO(generateAVAO(posArray, idxArray, color, GL_LINES));

        //testing purposes
        if(bb instanceof OBB){
            Vector3f center = ((OBB) bb).getCenter();
            float hx = ((OBB) bb).getHx();
            float hy = ((OBB) bb).getHy();
            float hz = ((OBB) bb).getHz();

            Vector3f x = ((OBB) bb).getX();
            Vector3f y = ((OBB) bb).getY();
            Vector3f z = ((OBB) bb).getZ();

            // Create Positions Array
            posArray = new float[] {
                    center.x, center.y, center.z,

                    center.x + hx, center.y, center.z,
                    center.x, center.y + hy, center.z,
                    center.x, center.y, center.z + hz,

                    center.x + x.x, center.y + x.y, center.z + x.z,
                    center.x + y.x, center.y + y.y, center.z + y.z,
                    center.x + z.x, center.y + z.y, center.z + z.z,
            };

            // Create Indices Array
            idxArray = new int[] {
                    0,1,
                    0,2,
                    0,3,

                    0,4,
                    0,5,
                    0,6,
            };

            renderer.addAlienVAO(generateAVAO(posArray, idxArray, color, GL_LINES));
        }

    }

    public static void drawAxis(Renderer renderer) {
        // todo: fix the GL hints here
        //glEnable(GL_LINE_SMOOTH);
        //glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        drawLine(new Vector3f(-20,0,0), new Vector3f(20,0,0), renderer, new Vector4f(255,0,0,0));
        drawLine(new Vector3f(0,-20,0), new Vector3f(0,20,0), renderer, new Vector4f(0,255,0,0));
        drawLine(new Vector3f(0,0,-20), new Vector3f(0,0,20), renderer, new Vector4f(0,0,255,0));
        //glDisable(GL_LINE_SMOOTH);
    }

    public static int[] getIntArray(ArrayList<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public static AlienVAO generateAVAO(float[] pos, int[] idx, Vector4f color, int drawMode) {

        ArrayList<Integer> vboIdList = new ArrayList<>();

        //setup vertex positions and buffer
        FloatBuffer posBuff = MemoryUtil.memAllocFloat(pos.length);
        posBuff.put(pos).flip();

        //setup indexes and buffer
        IntBuffer idxBuff = MemoryUtil.memAllocInt(idx.length);
        idxBuff.put(idx).flip();

        // get VAO
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Positions VBO
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuff, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        // Unbind attribute list VBOs -> only the Positions VBO in this case
        glBindBuffer(GL_ARRAY_BUFFER,0);

        // Indices VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuff, GL_STATIC_DRAW);

        // Unbid VAO
        glBindVertexArray(0);
        return new AlienVAO(vaoId, color, getIntArray(vboIdList), idx.length, drawMode);
    }

    public static void drawOBB() {
    }
}
