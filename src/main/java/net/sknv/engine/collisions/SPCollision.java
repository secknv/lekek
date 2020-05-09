package net.sknv.engine.collisions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.sknv.engine.GameItem;
import org.joml.Vector3f;
import java.util.*;

public class SPCollision implements ISweepPrune{
    private ArrayList<EndPoint> xAxis = new ArrayList<EndPoint>();
    private ArrayList<EndPoint> yAxis = new ArrayList<EndPoint>();
    private ArrayList<EndPoint> zAxis = new ArrayList<EndPoint>();
    private Table<BoundingBox, BoundingBox, Integer> collisionPairs = HashBasedTable.create();
    private int nItems = 0;

    @Override
    public void addItem(GameItem gameItem) throws Exception {//adds a game item to the sweep and prune algorithm (wip)
        BoundingBox bb = gameItem.getBoundingBox();
        if(nItems!=0){
            Set<BoundingBox> xCollisions = checkX(gameItem);
            Set<BoundingBox> yCollisions = checkY(gameItem);
            Set<BoundingBox> zCollisions = checkZ(gameItem);

            for (BoundingBox bb2 : xCollisions){
                if (yCollisions.contains(bb2) && zCollisions.contains(bb2)){
                    yCollisions.remove(bb2);
                    zCollisions.remove(bb2);
                    //throw new Exception("Object Colliding"); if spawning colliding item isn't allowed
                    collisionPairs.put(bb,bb2, 3);
                } else {
                    collisionPairs.put(bb, bb2, 1);
                }
            }

            for (BoundingBox bb2 : yCollisions){
               if (collisionPairs.contains(bb, bb2)){
                   increment(bb, bb2);
               } else {
                   collisionPairs.put(bb, bb2,  1);
               }
            }

            for (BoundingBox bb2 : zCollisions){
                if (collisionPairs.contains(bb, bb2)){
                    increment(bb, bb2);
                } else {
                    collisionPairs.put(bb, bb2,  1);
                }
            }

            insertItem(gameItem);
        } else {
            insertItem(gameItem);
        }
        nItems++;
        System.out.println(collisionPairs.values());
    }

    @Override
    public int updateItem(GameItem gameItem, Vector3f step) {
        BoundingBox bb = gameItem.getBoundingBox();
        final int[] nColl = {0};

        if(step.x!=0) tryMoveX(gameItem, step);
        if(step.y!=0) tryMoveY(gameItem, step);
        if(step.z!=0) tryMoveZ(gameItem, step);

        xAxis.sort((e1, e2) -> Float.compare(e1.getPosition().x, e2.getPosition().x));
        yAxis.sort((e1, e2) -> Float.compare(e1.getPosition().y, e2.getPosition().y));
        zAxis.sort((e1, e2) -> Float.compare(e1.getPosition().z, e2.getPosition().z));

        //maybe wont work for multiple updates (vese mais tarde)
        Map<BoundingBox, Integer> pairs = collisionPairs.column(bb);
        pairs.forEach((boundingBox, integer) -> {
            if (integer == 3){
                if(testCollision(bb, boundingBox)){
                    nColl[0]++;
                    //boundingBox.gameItem.nCollisions++;

                }
            }
        });

        Map<BoundingBox, Integer> pairs2 = collisionPairs.row(bb);
        pairs2.forEach((boundingBox, integer) -> {
            if (integer == 3){
                if(testCollision(bb, boundingBox)) {
                    nColl[0]++;
                    //boundingBox.gameItem.nCollisions++;

                }
            }
        });

        //if (nColl[0] != 0) System.out.println("collision");
        gameItem.nCollisions = nColl[0];

        return nColl[0];
    }

    @Override
    public void removeItem(GameItem gameItem) {

    }

    private void insertItem(GameItem gameItem){
        BoundingBox bb = gameItem.getBoundingBox();

        xAxis.add(bb.min);
        xAxis.add(bb.max);
        xAxis.sort((e1, e2) -> Float.compare(e1.getPosition().x, e2.getPosition().x));

        yAxis.add(bb.min);
        yAxis.add(bb.max);
        yAxis.sort((e1, e2) -> Float.compare(e1.getPosition().z, e2.getPosition().z));

        zAxis.add(bb.min);
        zAxis.add(bb.max);
        zAxis.sort((e1, e2) -> Float.compare(e1.getPosition().z, e2.getPosition().z));
    }


    private Set<BoundingBox> checkX(GameItem gameItem) {
        BoundingBox bb = gameItem.getBoundingBox();
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

    private Set<BoundingBox> checkY(GameItem gameItem) {
        BoundingBox bb = gameItem.getBoundingBox();
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

    private Set<BoundingBox> checkZ(GameItem gameItem) {
        BoundingBox bb = gameItem.getBoundingBox();
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

    private void tryMoveX(GameItem gameItem, Vector3f step){
        BoundingBox bb = gameItem.getBoundingBox();
        float nextMin = bb.getMin().getPosition().x + step.x;
        float nextMax = bb.getMax().getPosition().x + step.x;

        if (gameItem.velocity.x > 0){
            BoundingBox nextBb;
            int i = xAxis.indexOf(bb.getMax())+1;
            while (i < xAxis.size()-1 && i > -1 && xAxis.get(i).getPosition().x <= nextMax) {
                if (nextMax > xAxis.get(i).getPosition().x) {
                    nextBb = xAxis.get(i).getBB();
                    if (xAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, nextBb)) {//collision
                        incCollisions(bb, nextBb);
                    }
                }
                i++;
            }
            i = xAxis.indexOf(bb.getMin())+1;
            while (i < xAxis.size()-1 && i > -1 && xAxis.get(i).getPosition().x <= nextMin) {
                if (nextMax > xAxis.get(i).getPosition().x) {
                    nextBb = xAxis.get(i).getBB();
                    if (!xAxis.get(i).isMin() && !testCollisionX(nextMin, nextMax, nextBb)) {//out collision
                        decCollisions(bb, nextBb);
                    }
                }
                i++;
            }
        } else if(gameItem.velocity.x < 0){
            BoundingBox prevBb;
            int i = xAxis.indexOf(bb.getMin())-1;
            while (i > -1 && i < xAxis.size()-1 && xAxis.get(i).getPosition().x >= nextMin) {
                if (nextMin < xAxis.get(i).getPosition().x) {
                    prevBb = xAxis.get(i).getBB();
                    if (!xAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, prevBb)) {//collision
                        incCollisions(bb, prevBb);
                    }
                }
                i--;
            }
            i = xAxis.indexOf(bb.getMax())-1;
            while (i > -1 && i < xAxis.size()-1 && xAxis.get(i).getPosition().x >= nextMax) {
                if (nextMax < xAxis.get(i).getPosition().x) {
                    prevBb = xAxis.get(i).getBB();
                    if (xAxis.get(i).isMin() && !testCollisionX(nextMin, nextMax, prevBb)) {//out collision
                        decCollisions(bb, prevBb);
                    }
                }
                i--;
            }
        }
    }

    private void tryMoveY(GameItem gameItem, Vector3f step){
        BoundingBox bb = gameItem.getBoundingBox();
        float nextMin = bb.getMin().getPosition().y + step.y;
        float nextMax = bb.getMax().getPosition().y + step.y;

        if (gameItem.velocity.y > 0){
            BoundingBox nextBb;
            int i = yAxis.indexOf(bb.getMax())+1;
            float value = bb.getMax().getPosition().y;
            while (i < yAxis.size()-1 && i > -1 && yAxis.get(i).getPosition().y <= value) {
                if (bb.getMax().getPosition().y > yAxis.get(i).getPosition().y) {
                    nextBb = yAxis.get(i).getBB();
                    if (yAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, nextBb)) {//collision
                        incCollisions(bb, nextBb);
                    }
                }
                i++;
            }
            i = yAxis.indexOf(bb.getMin())+1;
            value = bb.getMin().getPosition().y;
            while (i < yAxis.size()-1 && i > -1 && yAxis.get(i).getPosition().y <= value) {
                if (bb.getMax().getPosition().y > yAxis.get(i).getPosition().y) {
                    nextBb = yAxis.get(i).getBB();
                    if (!yAxis.get(i).isMin() && !testCollisionX(nextMin, nextMax, nextBb)) {//out collision
                        decCollisions(bb, nextBb);
                    }
                }
                i++;
            }
        } else if(gameItem.velocity.y < 0){
            BoundingBox prevBb;
            int i = yAxis.indexOf(bb.getMin())-1;
            float value = bb.getMin().getPosition().y;
            while (i > -1 && i < yAxis.size()-1 && yAxis.get(i).getPosition().y >= value) {
                if (bb.getMin().getPosition().y < yAxis.get(i).getPosition().y) {
                    prevBb = yAxis.get(i).getBB();
                    if (!yAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, prevBb)) {//collision
                        incCollisions(bb, prevBb);
                    }
                }
                i--;
            }
            i = yAxis.indexOf(bb.getMax())-1;
            value = bb.getMax().getPosition().y;
            while (i > -1 && i < yAxis.size()-1 && yAxis.get(i).getPosition().y >= value) {
                if (bb.getMax().getPosition().y < yAxis.get(i).getPosition().y) {
                    prevBb = yAxis.get(i).getBB();
                    if (yAxis.get(i).isMin() && !testCollisionX(nextMin, nextMax, prevBb)) {//out collision
                        decCollisions(bb, prevBb);
                    }
                }
                i--;
            }
        }
    }

    private void tryMoveZ(GameItem gameItem, Vector3f step){
        BoundingBox bb = gameItem.getBoundingBox();
        float nextMin = bb.getMin().getPosition().z + step.z;
        float nextMax = bb.getMax().getPosition().z + step.z;

        if (gameItem.velocity.z > 0){
            BoundingBox nextBb;
            int i = zAxis.indexOf(bb.getMax())+1;
            while (i < zAxis.size()-1 && i > -1 && zAxis.get(i).getPosition().z <= nextMax) {
                if (nextMax > zAxis.get(i).getPosition().z) {
                    nextBb = zAxis.get(i).getBB();
                    if (zAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, nextBb)) {//collision
                        incCollisions(bb, nextBb);
                    }
                }
                i++;
            }
            i = zAxis.indexOf(bb.getMin())+1;
            while (i < zAxis.size()-1 && i > -1 && zAxis.get(i).getPosition().z <= nextMin) {
                if (nextMax > zAxis.get(i).getPosition().z) {
                    nextBb = zAxis.get(i).getBB();
                    if (!zAxis.get(i).isMin() && !testCollisionX(nextMin, nextMax, nextBb)) {//out collision
                        decCollisions(bb, nextBb);
                    }
                }
                i++;
            }
        } else if(gameItem.velocity.z < 0){
            BoundingBox prevBb;
            int i = zAxis.indexOf(bb.getMin())-1;
            while (i > -1 && i < zAxis.size()-1 && zAxis.get(i).getPosition().z >= nextMin) {
                if (nextMin < zAxis.get(i).getPosition().z) {
                    prevBb = zAxis.get(i).getBB();
                    if (!zAxis.get(i).isMin() && testCollisionX(nextMin, nextMax, prevBb)) {//collision
                        incCollisions(bb, prevBb);
                    }
                }
                i--;
            }
            i = zAxis.indexOf(bb.getMax())-1;
            while (i > -1 && i < zAxis.size()-1 && zAxis.get(i).getPosition().z >= nextMax) {
                if (nextMax < zAxis.get(i).getPosition().z) {
                    prevBb = zAxis.get(i).getBB();
                    if (zAxis.get(i).isMin() && !testCollisionX(nextMin, nextMax, prevBb)) {//out collision
                        decCollisions(bb, prevBb);
                    }
                }
                i--;
            }
        }
    }

    private void incCollisions(BoundingBox bb, BoundingBox bb2) {
        if (collisionPairs.contains(bb, bb2)){
                increment(bb, bb2);
        } else if (collisionPairs.contains(bb2, bb)){
                increment(bb2, bb);
        } else {
            collisionPairs.put(bb, bb2, 1);
        }
    }

    private void decCollisions(BoundingBox bb, BoundingBox bb2) {
        if (collisionPairs.contains(bb, bb2)){
            decrement(bb, bb2);
        } else if (collisionPairs.contains(bb2, bb)){
            decrement(bb2, bb);
        } else {
            collisionPairs.put(bb, bb2, 0);
        }
    }

    private void increment(BoundingBox bb, BoundingBox bb2) {
        if(collisionPairs.get(bb, bb2) < 3) collisionPairs.put(bb, bb2, collisionPairs.get(bb, bb2) + 1);
    }

    private void decrement(BoundingBox bb, BoundingBox bb2) {
        if(collisionPairs.get(bb, bb2) > 0) collisionPairs.put(bb, bb2, collisionPairs.get(bb, bb2) - 1);
    }

    private boolean testCollision(BoundingBox bb, BoundingBox bb2) {
        return testCollisionX(bb.getMin().getPosition().x, bb.getMax().getPosition().x, bb2)
                && testCollisionY(bb.getMin().getPosition().y, bb.getMax().getPosition().y, bb2)
                && testCollisionZ(bb.getMin().getPosition().z, bb.getMax().getPosition().z, bb2);
    }

    private boolean testCollisionX(float min, float max, BoundingBox bb2) {
        return !(max<=bb2.getMin().getPosition().x || min>=bb2.getMax().getPosition().x);
    }

    private boolean testCollisionY(float min, float max, BoundingBox bb2) {
        return !(max<=bb2.getMin().getPosition().y || min>=bb2.getMax().getPosition().y);
    }

    private boolean testCollisionZ(float min, float max, BoundingBox bb2) {
        return !(max<=bb2.getMin().getPosition().z || min>=bb2.getMax().getPosition().z);
    }

    public void printAxis() {
        System.out.println(xAxis);
        System.out.println(yAxis);
        System.out.println(zAxis);
    }
}
