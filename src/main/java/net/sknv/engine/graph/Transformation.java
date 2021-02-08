package net.sknv.engine.graph;

import net.sknv.engine.entities.AbstractGameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private static final Matrix4f projectionMatrix = new Matrix4f();
    private static final Matrix4f viewMatrix = new Matrix4f();
    private static final Matrix4f modelViewMatrix = new Matrix4f();
    private static final Matrix4f orthogonalMatrix = new Matrix4f();

    public static Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public static Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rot = camera.getRotation();

        viewMatrix.identity();
        //must rotate first so camera rotates over it's position
        viewMatrix.rotate(rot.x, new Vector3f(1, 0, 0))
                .rotate(rot.y, new Vector3f(0, 1, 0));
        //then do translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public static Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top) {
        orthogonalMatrix.identity();
        orthogonalMatrix.setOrtho2D(left, right, bottom, top);
        return orthogonalMatrix;
    }


    public static Matrix4f getModelViewMatrix(AbstractGameItem item, Matrix4f viewMatrix) {// modelViewMatrix = modelMatrix * viewMatrix
        //must create copy
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        //because this changes the values of viewCurr and since there is only one viewMatrix for all the items,
        //the values would accumulate every time this method ran
        return viewCurr.mul(getModelMatrix(item));
    }

    public static Matrix4f getOrtoProjModelMatrix(AbstractGameItem gameItem, Matrix4f orthoMatrix) {
        Matrix4f modelMatrix = getModelMatrix(gameItem);
        Matrix4f orthoMatrixCurr = new Matrix4f(orthoMatrix);
        orthoMatrixCurr.mul(modelMatrix);
        return orthoMatrixCurr;
    }

    public static Matrix4f getModelMatrix(AbstractGameItem gameItem){
        Vector3f rotation = gameItem.getRotation();
        modelViewMatrix.identity().translate(gameItem.getPosition()).
                rotateX(rotation.x).
                rotateY(rotation.y).
                rotateZ(rotation.z).
                scale(gameItem.getScale());
        return modelViewMatrix;
    }

}
