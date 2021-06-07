package net.sknv.engine.entities;

import net.sknv.engine.Utils;
import net.sknv.engine.graph.*;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GameItemMesh extends AbstractGameItem {

    protected transient Mesh mesh;

    public GameItemMesh() {
        super();
    }

    public GameItemMesh(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    @Override
    public void render(ShaderProgram shaderProgram, Matrix4f viewMatrix) {

        int drawMode = mesh.getDrawMode();

        Matrix4f transformationResult = Transformation.getModelViewMatrix(this, viewMatrix);

        shaderProgram.setUniform("modelViewMatrix", transformationResult);
        shaderProgram.setUniform("material", mesh.getMaterial());

        // this part used to be on Mesh::render
        Texture texture = mesh.getMaterial().getTexture();
        if (texture != null) {
            //tell openGL to use first texture bank and bind texture
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        else {
            // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        //draw mesh
        glBindVertexArray(mesh.getVaoId());

        glDrawElements(drawMode, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        //restore state
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public String toString() {
        return "GameItemMesh{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                ", mesh=" + mesh +
                '}';
    }

    private void readObject(java.io.ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        Mesh mesh = OBJLoader.loadMesh((String) inputStream.readObject());
        mesh.setMaterial((Material) inputStream.readObject());

        setMesh(mesh);
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();
        outputStream.writeObject(mesh.getModelFile());
        outputStream.writeObject(mesh.getMaterial());
    }

    /*
    * // todo: this is probably spaghet but ok
    * The idea here is to have Collider override this method, call super and add the BB rotation line.
    * BUT `this.boundingBox.rotate(rotQuaternion);` <- requires rotQuaternion
    * SO we make this method return that
    * */
    public Quaternionf rotateEuclidean(Vector3f rot) {
        // Object POV axis
        Vector3f xAxis = new Vector3f(1,0,0);
        Vector3f yAxis = new Vector3f(0,1,0);
        Vector3f zAxis = new Vector3f(0,0,1);

        //quaternions to get to current rot
        Quaternionf current = new Quaternionf(new AxisAngle4f(this.getRotation().x, xAxis));
        Quaternionf curY = new Quaternionf(new AxisAngle4f(this.getRotation().y, yAxis));
        Quaternionf curZ = new Quaternionf(new AxisAngle4f(this.getRotation().z, zAxis));
        current.mul(curY).mul(curZ);

        // generate rotated object axis'
        current.transform(xAxis);
        current.transform(yAxis);
        current.transform(zAxis);

        Quaternionf xq = new Quaternionf(new AxisAngle4f(rot.x, xAxis));
        Quaternionf yq = new Quaternionf(new AxisAngle4f(rot.y, yAxis));
        Quaternionf zq = new Quaternionf(new AxisAngle4f(rot.z, zAxis));

        // get rotation on world axis for setRotation
        xq.mul(yq).mul(zq);

        Quaternionf rotQuaternion = new Quaternionf();
        xq.get(rotQuaternion);

        //combine
        xq.mul(current);

        rotation = Utils.getEulerAngles(xq);//set item rot
        return rotQuaternion;
    }

    public void setRotationEuclidean(Vector3f euclideanRot) {
        euclideanRot.sub(rotation);
        rotateEuclidean(euclideanRot);
    }

}
