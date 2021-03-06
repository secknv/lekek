package net.sknv.engine.physics.collisionDetection;

import net.sknv.engine.entities.Collider;
import net.sknv.engine.physics.colliders.BoundingBox;
import net.sknv.engine.physics.colliders.EndPoint;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SPCollision implements ISweepPrune {
    private ArrayList<EndPoint> xAxis = new ArrayList<>();
    private ArrayList<EndPoint> yAxis = new ArrayList<>();
    private ArrayList<EndPoint> zAxis = new ArrayList<>();
    private int nItems = 0;

    @Override
    public Set<BoundingBox> addItem(Collider collider){
        Set<BoundingBox> xCollisions = checkAddX(collider);
        Set<BoundingBox> yCollisions = checkAddY(collider);
        Set<BoundingBox> zCollisions = checkAddZ(collider);

        if(nItems>0){
            xCollisions.retainAll(yCollisions);
            xCollisions.retainAll(zCollisions);
            //if (!xCollisions.isEmpty()) throw new Exception("Object colliding"); //prevent adding colliding item
        }

        insertItem(collider);
        return xCollisions;
    }

    @Override
    public Set<BoundingBox> checkStepCollisions(Collider collider, Vector3f step) {
        sortAxis();

        BoundingBox bb = collider.getBoundingBox();
        Vector3f nextMin = new Vector3f(), nextMax = new Vector3f();

        nextMin.add(bb.getMin().getPosition()).add(step);
        nextMax.add(bb.getMax().getPosition()).add(step);

        HashSet<BoundingBox> possibleCollisions = new HashSet<>();

        if(step.x!=0) possibleCollisions.addAll(checkStepX(collider, step.x));
        if(step.y!=0) possibleCollisions.addAll(checkStepY(collider, step.y));
        if(step.z!=0) possibleCollisions.addAll(checkStepZ(collider, step.z));

        Set<BoundingBox> collidingBoxes = new HashSet<>();

        for(BoundingBox box : possibleCollisions){
            if (testCollision(nextMin, nextMax, box)) collidingBoxes.add(box);
        }

        return collidingBoxes;
    }

    @Override
    public void removeItem(Collider collider) {
        BoundingBox bb = collider.getBoundingBox();
        xAxis.remove(bb.getMin());
        xAxis.remove(bb.getMax());
        yAxis.remove(bb.getMin());
        yAxis.remove(bb.getMax());
        zAxis.remove(bb.getMin());
        zAxis.remove(bb.getMax());
        nItems--;
    }

    private void insertItem(Collider collider){
        BoundingBox bb = collider.getBoundingBox();

        xAxis.add(bb.getMin());
        xAxis.add(bb.getMax());

        yAxis.add(bb.getMin());
        yAxis.add(bb.getMax());

        zAxis.add(bb.getMin());
        zAxis.add(bb.getMax());

        nItems++;
        sortAxis();
    }


    private Set<BoundingBox> checkAddX(Collider collider) {
        BoundingBox bb = collider.getBoundingBox();
        EndPoint min, max;
        Set<BoundingBox> possibleCollisions = new HashSet<>();

        int i=0;
        while (i!= xAxis.size()){
            if (xAxis.get(i).isMin()){//min
                min = xAxis.get(i);
                max = min.getBB().getMax();

            } else {//max
                max = xAxis.get(i);
                min = max.getBB().getMin();
            }

            if (bb.getMin().getPosition().x <= min.getPosition().x){
                if(bb.getMax().getPosition().x >= min.getPosition().x ) possibleCollisions.add(min.getBB());
            } else {
                if(bb.getMin().getPosition().x <= max.getPosition().x) possibleCollisions.add(min.getBB());
            }
            i++;
        }
        return possibleCollisions;
    }

    private Set<BoundingBox> checkAddY(Collider collider) {
        BoundingBox bb = collider.getBoundingBox();
        EndPoint min, max;
        Set<BoundingBox> possibleCollisions = new HashSet<>();

        int i=0;
        while (i!= yAxis.size()){
            if (yAxis.get(i).isMin()){//min
                min = yAxis.get(i);
                max = min.getBB().getMax();

            } else {//max
                max = yAxis.get(i);
                min = max.getBB().getMin();
            }

            if (bb.getMin().getPosition().y <= min.getPosition().y){
                if(bb.getMax().getPosition().y >= min.getPosition().y) possibleCollisions.add(min.getBB());
            } else {
                if(bb.getMin().getPosition().y <= max.getPosition().y) possibleCollisions.add(min.getBB());
            }
            i++;
        }
        return possibleCollisions;
    }

    private Set<BoundingBox> checkAddZ(Collider collider) {
        BoundingBox bb = collider.getBoundingBox();
        EndPoint min, max;
        Set<BoundingBox> possibleCollisions = new HashSet<>();

        int i=0;
        while (i!= zAxis.size()){
            if (zAxis.get(i).isMin()){//min
                min = zAxis.get(i);
                max = min.getBB().getMax();

            } else {//max
                max = zAxis.get(i);
                min = max.getBB().getMin();
            }

            if (bb.getMin().getPosition().z <= min.getPosition().z){
                if(bb.getMax().getPosition().z >= min.getPosition().z) possibleCollisions.add(min.getBB());
            } else {
                if(bb.getMin().getPosition().z <= max.getPosition().z) possibleCollisions.add(min.getBB());
            }
            i++;
        }
        return possibleCollisions;
    }

    private HashSet<BoundingBox> checkStepX(Collider collider, float stepX){
        HashSet<BoundingBox> collisions = new HashSet<>();
        BoundingBox bb = collider.getBoundingBox();
        float nextMin = bb.getMin().getPosition().x + stepX;
        float nextMax = bb.getMax().getPosition().x + stepX;

        if (stepX > 0){
            BoundingBox nextBb;
            int i = xAxis.indexOf(bb.getMax())+1;
            while (i < xAxis.size()-1 && i > -1 && xAxis.get(i).getPosition().x < nextMax) {
                nextBb = xAxis.get(i).getBB();
                if (xAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, nextBb)) {//collision
                    collisions.add(nextBb);
                }
                i++;
            }
        } else {
            BoundingBox prevBb;
            int i = xAxis.indexOf(bb.getMin())-1;
            while (i > -1 && i < xAxis.size()-1 && xAxis.get(i).getPosition().x >= nextMin) {
                prevBb = xAxis.get(i).getBB();
                if (!xAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, prevBb)) {//collision
                    collisions.add(prevBb);
                }
                i--;
            }
        }
        return collisions;
    }

    private HashSet<BoundingBox> checkStepY(Collider collider, float stepY){
        HashSet<BoundingBox> collisions = new HashSet<>();
        BoundingBox bb = collider.getBoundingBox();
        float nextMin = bb.getMin().getPosition().y + stepY;
        float nextMax = bb.getMax().getPosition().y + stepY;

        if (stepY > 0){
            BoundingBox nextBb;
            int i = yAxis.indexOf(bb.getMax())+1;
            while (i < yAxis.size()-1 && i > -1 && yAxis.get(i).getPosition().y < nextMax) {
                nextBb = yAxis.get(i).getBB();
                    if (yAxis.get(i).isMin() && testCollisionY(nextMin, nextMax, nextBb)) {//collision
                        collisions.add(nextBb);
                    }
                i++;
            }
        } else {
            BoundingBox prevBb;
            int i = yAxis.indexOf(bb.getMin())-1;
            while (i > -1 && i < yAxis.size()-1 && yAxis.get(i).getPosition().y >= nextMin) {
                prevBb = yAxis.get(i).getBB();
                    if (!yAxis.get(i).isMin() && testCollisionY(nextMin, nextMax, prevBb)) {//collision
                        collisions.add(prevBb);
                    }
                i--;
            }
        }
        return collisions;
    }

    private HashSet<BoundingBox> checkStepZ(Collider collider, float stepZ){
        HashSet<BoundingBox> collisions = new HashSet<>();
        BoundingBox bb = collider.getBoundingBox();
        float nextMin = bb.getMin().getPosition().z + stepZ;
        float nextMax = bb.getMax().getPosition().z + stepZ;

        if (stepZ > 0){
            BoundingBox nextBb;
            int i = zAxis.indexOf(bb.getMax())+1;
            while (i < zAxis.size()-1 && i > -1 && zAxis.get(i).getPosition().z < nextMax) {
                nextBb = zAxis.get(i).getBB();
                if (zAxis.get(i).isMin() && testCollisionZ(nextMin, nextMax, nextBb)) {//collision
                    collisions.add(nextBb);
                }
                i++;
            }
        } else {
            BoundingBox prevBb;
            int i = zAxis.indexOf(bb.getMin())-1;
            while (i > -1 && i < zAxis.size()-1 && zAxis.get(i).getPosition().z >= nextMin) {
                prevBb = zAxis.get(i).getBB();
                if (!zAxis.get(i).isMin() && testCollisionZ(nextMin, nextMax, prevBb)) {//collision
                    collisions.add(prevBb);
                }
                i--;
            }
        }
        return collisions;
    }

    private boolean testCollision(Vector3f min, Vector3f max, BoundingBox bb2) {
        return testCollisionX(min.x, max.x, bb2) && testCollisionY(min.y, max.y, bb2) && testCollisionZ(min.z, max.z, bb2);
    }

    private boolean testCollisionX(float min, float max, BoundingBox bb2) {
        return !(max<bb2.getMin().getPosition().x || min>bb2.getMax().getPosition().x);
    }

    private boolean testCollisionY(float min, float max, BoundingBox bb2) {
        return !(max<bb2.getMin().getPosition().y || min>bb2.getMax().getPosition().y);
    }

    private boolean testCollisionZ(float min, float max, BoundingBox bb2) {
        return !(max<bb2.getMin().getPosition().z || min>bb2.getMax().getPosition().z);
    }

    public void sortAxis(){
        xAxis.sort((e1, e2) -> Float.compare(e1.getPosition().x, e2.getPosition().x));
        yAxis.sort((e1, e2) -> Float.compare(e1.getPosition().y, e2.getPosition().y));
        zAxis.sort((e1, e2) -> Float.compare(e1.getPosition().z, e2.getPosition().z));
    }

    @Override
    public String toString() {
        return "x= " + xAxis +
                "y= " + yAxis +
                "z= " + zAxis;
    }
}
