package net.sknv.engine.entities;

import net.sknv.engine.Utils;
import net.sknv.engine.graph.*;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class aggregates all common GameItem code for the different implementations of {@link IRenderable#render(ShaderProgram, Matrix4f)}.
 */
public abstract class AbstractGameItem implements IRenderable, Serializable {

    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;

    protected transient Mesh mesh;
    public AbstractGameItem() {
        // this still exists because of TextItem calling it
        // but spaghet
        // todo: fix AbsGameItem should always construct with a mesh
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
    }
    public AbstractGameItem(Mesh mesh) {
        this();
        setMesh(mesh);
    }

    public void translate(Vector3f step) {
        this.position.x += step.x;
        this.position.y += step.y;
        this.position.z += step.z;
    }

    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getRotation() {
        return rotation;
    }
    public float getScale() {
        return scale;
    }
    public Mesh getMesh() {
        return mesh;
    }

    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x,y,z));
    }
    public void setPosition(Vector3f position){
        this.position = position;
    }
    public void setRotation(float x, float y, float z) {
        setRotation(new Vector3f(x,y,z));
    }
    public void setRotation(Vector3f rotation){
        this.rotation = rotation;
    }
    public void setScale(float scale) {
        this.scale = scale;
    }
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
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

    @Override
    public String toString() {
        return "AbstractGameItem{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                ", mesh=" + mesh +
                '}';
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
