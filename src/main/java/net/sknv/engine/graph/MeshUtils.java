package net.sknv.engine.graph;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class MeshUtils {
    public static Mesh generateCube(WebColor color, int drawmode) {
        return generateCube(color, 1, 1, drawmode);
    }

    public static Mesh generateCube(WebColor color, float alpha, float reflectance, int drawmode) {
        return generateCube(new Material(color, alpha, reflectance), drawmode);
    }

    public static Mesh generateCube(Vector4f color, float reflectance, int drawmode) {
        return generateCube(new Material(color, reflectance), drawmode);
    }

    public static Mesh generateCube(Material material, int drawMode) {
        float[] posArr = new float[] {1, -1, -1, 1, -1, 1, -1, -1, 1, -1, -1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1, -1, 1, -1, 1, -1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, -1, -1, -1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, -1, -1, 1, -1};
        float[] normalsArr = new float[] {0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
        int[] idxArr = new int[] {10, 16, 12, 23, 21, 19, 14, 18, 11, 5, 20, 17, 2, 6, 22, 0, 3, 7, 8, 10, 12, 15, 23, 19, 9, 14, 11, 1, 5, 17, 13, 2, 22, 4, 0, 7};

        float[] texCoordsArr = new float[1];
        Mesh mesh = new Mesh(posArr, normalsArr, texCoordsArr, idxArr, drawMode);
        mesh.setMaterial(material);
        return mesh;
    }

    public static Mesh generateLine(WebColor color, Vector3f start, Vector3f end) {

        // Create Vertex Positions Array
        float[] posArray = new float[]{start.x,start.y,start.z,end.x,end.y,end.z};
        // Create Indices Array
        int[] idxArray = new int[]{0,1};
        Mesh mesh = new Mesh(posArray,idxArray, GL_LINES);
        // mesh must be able to no recieve textcoords, normas
        mesh.setMaterial(new Material(color, 1, 1));
        return mesh;
    }
}
