package net.sknv.engine.graph;

import net.sknv.engine.Utils;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class MeshUtils {

    public static Mesh buildQuad(Vector4f color) {

        float[] textCoordsArr = {0, 0};
        float[] normals = {0, 0, 0};

        float[] posArr = {
                0, 1, 0,
                0, 0, 0,
                1, 0, 0,
                1, 1, 0
        };

        int[] indicesArr = {0, 1, 3, 3, 1, 2};

        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(color, 0.5f));
        return mesh;
    }

    public static Mesh buildLine(Vector4f color, Vector3f start, Vector3f end) {
        float[] textCoordsArr = {0, 0};
        float[] normals = {0, 0, 0};

        float[] posArr = {
                start.x, start.y, start.z,
                end.x, end.y, end.z
        };

        int[] idxArr = {0, 1};

        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, idxArr, GL_LINES);
        mesh.setMaterial(new Material(color, 0.5f));
        return mesh;
    }

    public static Mesh buildGrid(Vector3f origin, int size) {

        //setup vertex positions
        float[] posArr = new float[3 * 4 * size];

        Vector3f start = new Vector3f();
        origin.add(-size/2f, 0f, -size/2f, start);

        for (int i = 0; i != 4 * size; i++) {
            if (i < size) {
                posArr[3 * i] = start.x;
                posArr[3 * i + 1] = start.y;
                posArr[3 * i + 2] = start.z;
                start.add(new Vector3f(1, 0, 0));
            } else if (i < 2 * size) {
                posArr[3 * i] = start.x;
                posArr[3 * i + 1] = start.y;
                posArr[3 * i + 2] = start.z;
                start.add(new Vector3f(0, 0, 1));
            } else if (i < 3 * size) {
                posArr[3 * i] = start.x;
                posArr[3 * i + 1] = start.y;
                posArr[3 * i + 2] = start.z;
                start.add(new Vector3f(-1, 0, 0));
            } else {
                posArr[3 * i] = start.x;
                posArr[3 * i + 1] = start.y;
                posArr[3 * i + 2] = start.z;
                start.add(new Vector3f(0, 0, -1));
            }
        }

        //setup indexes array
        int[] idxArr = new int[4 * (size + 1)];
        idxArr[idxArr.length - 1] = 0;
        idxArr[idxArr.length - 2] = size;
        for (int i = 0; i != (idxArr.length / 2) - 1; i++) {
            idxArr[i * 2] = i;
            if (i < size + 1) idxArr[i * 2 + 1] = 3 * size - i;
            else idxArr[i * 2 + 1] = 5 * size - i;
        }

        float[] textCoordsArr = {0, 0};
        float[] normals = {0, 0, 0};

        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, idxArr, GL_LINES);
        mesh.setMaterial(new Material(new Vector4f(1, 1, 1, 1), 0.5f));
        return mesh;
    }
}
