package net.sknv.engine.entities;

import net.sknv.engine.graph.IRenderable;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;

/**
 * This class aggregates all common GameItem code for the different implementations of {@link IRenderable#render(ShaderProgram, Matrix4f)}.
 */
public abstract class AbstractGameItem implements IRenderable, Serializable {

    protected Vector3f position;
    //protected Vector3f rotation;
    protected Quaternionf rotation;
    protected float scale;

    protected Mesh mesh;
    public AbstractGameItem() {
        // this still exists because of TextItem calling it
        // but spaghet
        // todo: fix AbsGameItem should always construct with a mesh
        position = new Vector3f(0, 0, 0);
        //rotation = new Vector3f(0, 0, 0);
        rotation = new Quaternionf();
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
//    public Vector3f getRotation() {
//        return rotation;
//    }
    public Quaternionf getRotation() {
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
//    public void setRotation(float x, float y, float z) {
//        setRotation(new Vector3f(x,y,z));
//    }
//    public void setRotation(Vector3f rotation){
//        this.rotation = rotation;
//    }
    public void setRotation(Quaternionf rotation){
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
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
        Quaternionf rotation = new Quaternionf().rotationXYZ(rot.x, rot.y, rot.z);
        rotate(rotation);

        return rotation;
    }

    public void rotate(Quaternionf rotation){
        this.rotation.mul(rotation);
    }

    public void setRotationEuclidean(Vector3f euclideanRot) {
        rotation.rotationXYZ(euclideanRot.x, euclideanRot.y, euclideanRot.z);
    }
}
