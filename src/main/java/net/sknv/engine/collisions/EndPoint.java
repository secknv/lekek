package net.sknv.engine.collisions;

import org.joml.Vector3f;

public class EndPoint {
    private BoundingBox boundingBox; //AABB
    private Vector3f position;
    private boolean isMin;

    public EndPoint(BoundingBox box, Vector3f position, boolean isMin) {
        this.boundingBox = box;
        this.position = position;
        this.isMin = isMin;
    }

    @Override
    public String toString() {
        String min = isMin ? "min" : "max";
        return  position + " " + min;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getX(){
        return this.position.x;
    }

    public float getY(){
        return this.position.y;
    }

    public float getZ(){
        return this.position.z;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setX(float x){
        this.position.x = x;
    }

    public void setY(float y){
        this.position.y = y;
    }

    public void setZ(float z){
        this.position.z = z;
    }

    public boolean isMin() {
        return isMin;
    }

    public BoundingBox getBB() {
        return boundingBox;
    }

}
