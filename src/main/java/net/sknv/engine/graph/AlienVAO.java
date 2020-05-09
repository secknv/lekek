package net.sknv.engine.graph;

import org.joml.Vector4f;

public class AlienVAO {
    private final int vaoId;
    private final int[] vboIds;
    private final int drawMode;
    private final int vertexCount;
    private final Vector4f color;

    public AlienVAO(int vaoId, Vector4f color, int[] vboIds, int vertexCount, int drawMode){
        this.vaoId = vaoId;
        this.vboIds = vboIds;
        this.drawMode = drawMode;
        this.vertexCount = vertexCount;
        this.color = color;
    }

    public int getVaoId() {
        return vaoId;
    }
    public Vector4f getColor() {
        return color;
    }

    public int[] getVboIds() {
        return vboIds;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
