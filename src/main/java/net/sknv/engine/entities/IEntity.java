package net.sknv.engine.entities;

import net.sknv.engine.BoundingBox;
import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

public interface IEntity {

    void setPos(float x, float y, float z);
    Vector3f getPos();

    void setRot(float x, float y, float z);
    Vector3f getRot();

    void setScale(float scale);
    float getScale();

    void setMesh(Mesh mesh);
    Mesh getMesh();

    void setBoundingBox(BoundingBox boundingBox);
    BoundingBox getBoundingBox();

}
