package net.sknv.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix, worldMatrix;

    public Transformation() {
        worldMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rot, float scale) {
        worldMatrix.identity().translate(offset).
                rotateX((float)Math.toRadians(rot.x)).
                rotateY((float)Math.toRadians(rot.y)).
                rotateZ((float)Math.toRadians(rot.z)).
                scale(scale);
        return worldMatrix;
    }
}
