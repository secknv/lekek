package net.sknv.engine.graph;

import org.joml.Vector4f;

public class MeshUtils {
    public static Mesh generateCube(WebColor color) {
        return generateCube(color, 1, 1);
    }

    public static Mesh generateCube(WebColor color, float alpha, float reflectance) {
        return generateCube(new Material(color, alpha, reflectance));
    }

    public static Mesh generateCube(Vector4f color, float reflectance) {
        return generateCube(new Material(color, reflectance));
    }

    public static Mesh generateCube(Material material) {
        float[] posArr = new float[] {1, -1, -1, 1, -1, 1, -1, -1, 1, -1, -1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1, -1, 1, -1, 1, -1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, -1, -1, -1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, -1, -1, 1, -1};
        float[] normalsArr = new float[] {0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
        int[] idxArr = new int[] {10, 16, 12, 23, 21, 19, 14, 18, 11, 5, 20, 17, 2, 6, 22, 0, 3, 7, 8, 10, 12, 15, 23, 19, 9, 14, 11, 1, 5, 17, 13, 2, 22, 4, 0, 7};

        float[] texCoordsArr = new float[1];
        Mesh mesh = new Mesh(posArr, normalsArr, texCoordsArr, idxArr);
        mesh.setMaterial(material);
        return mesh;
    }
}
