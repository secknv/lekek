package net.sknv.engine.graph;

import net.sknv.engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix, viewMatrix, modelViewMatrix, orthoMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rot = camera.getRotation();

        viewMatrix.identity();
        //must rotate first so camera rotates over it's position
        viewMatrix.rotate((float)Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rot.y), new Vector3f(0, 1, 0));
        //then do translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top) {
        orthoMatrix.identity();
        orthoMatrix.setOrtho2D(left, right, bottom, top);
        return orthoMatrix;
    }


    public Matrix4f getModelViewMatrix(GameItem item, Matrix4f viewMatrix) {// modelViewMatrix = modelMatrix * viewMatrix
        //must create copy
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        //because this changes the values of viewCurr and since there is only one viewMatrix for all the items,
        //the values would accumulate every time this method ran
        return viewCurr.mul(getModelMatrix(item));
    }

    public Matrix4f getOrtoProjModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        Vector3f rotation = gameItem.getRot();
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity().translate(gameItem.getPos()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        Matrix4f orthoMatrixCurr = new Matrix4f(orthoMatrix);
        orthoMatrixCurr.mul(modelMatrix);
        return orthoMatrixCurr;
    }

    public Matrix4f getModelMatrix(GameItem item){
        Vector3f rotation = item.getRot();
        modelViewMatrix.identity().translate(item.getPos()).
                rotateX(rotation.x).
                rotateY(rotation.y).
                rotateZ(rotation.z).
                scale(item.getScale());
        return modelViewMatrix;
    }

}
