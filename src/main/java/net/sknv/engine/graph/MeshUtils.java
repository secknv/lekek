package net.sknv.engine.graph;

import net.sknv.engine.Utils;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshUtils {

    public static Mesh buildQuad(Vector4f color, Vector3f topLeft, Vector3f botLeft, Vector3f botRight, Vector3f topRight) {

        List<Vector3f> points = new ArrayList<>();
        points.add(topLeft);
        points.add(botLeft);
        points.add(botRight);
        points.add(topRight);

        float[] posArr = new float[4*3];
        float[] textCoordsArr = new float[0];
        float[] normals = new float[0];

        for(int i = 0; i<points.size(); i++) {
            posArr[3*i] = points.get(i).x;
            posArr[3*i+1] = points.get(i).y;
            posArr[3*i+2] = points.get(i).z;
        }

        int[] indicesArr = {0, 1, 3, 3, 1, 2};

        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(color, 0.5f));
        return mesh;
    }
}
