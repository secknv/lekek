package net.sknv.engine.collisions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.sknv.engine.BoundingBox;
import net.sknv.engine.GameItem;
import org.joml.Vector3f;
import java.util.*;

public class SPCollisions implements ISweepPrune{
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
                    throw new Exception("Object Colliding");
                }
                else {
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
    public void updateItem(GameItem gameItem) {
        BoundingBox bb = gameItem.getBoundingBox();
        final int[] nColl = {0};

        Vector3f accel = gameItem.tryMove();
        if(accel.x!=0) tryMoveX(gameItem, bb);
        if(accel.y!=0) tryMoveY(gameItem, bb);
        if(accel.z!=0) tryMoveZ(gameItem, bb);

        xAxis.sort((e1, e2) -> Float.compare(e1.position, e2.position));
        yAxis.sort((e1, e2) -> Float.compare(e1.position, e2.position));
        zAxis.sort((e1, e2) -> Float.compare(e1.position, e2.position));

        System.out.println(collisionPairs.values());

        //maybe wont work for multiple updates (vese mais tarde)
        Map<BoundingBox, Integer> pairs = collisionPairs.column(bb);
        pairs.forEach((boundingBox, integer) -> {
            if (integer == 3){
                boundingBox.gameItem.isColliding = true;
                gameItem.isColliding = true;
                nColl[0]++;
            } else {
                boundingBox.gameItem.isColliding = false;
            }
        });

        Map<BoundingBox, Integer> pairs2 = collisionPairs.row(bb);
        pairs2.forEach((boundingBox, integer) -> {
            if (integer == 3){
                boundingBox.gameItem.isColliding = true;
                gameItem.isColliding = true;
                nColl[0]++;
            } else {
                boundingBox.gameItem.isColliding = false;
            }
        });

        if (nColl[0] == 0) gameItem.isColliding = false;
    }

    @Override
    public void removeItem(GameItem gameItem) {

    }

    private void insertItem(GameItem gameItem){
        xAxis.add(gameItem.getBoundingBox().xMin);
        xAxis.add(gameItem.getBoundingBox().xMax);
        xAxis.sort((e1, e2) -> Float.compare(e1.position, e2.position));
        yAxis.add(gameItem.getBoundingBox().yMin);
        yAxis.add(gameItem.getBoundingBox().yMax);
        yAxis.sort((e1, e2) -> Float.compare(e1.position, e2.position));
        zAxis.add(gameItem.getBoundingBox().zMin);
        zAxis.add(gameItem.getBoundingBox().zMax);
        zAxis.sort((e1, e2) -> Float.compare(e1.position, e2.position));
    }


    private Set<BoundingBox> checkX(GameItem gameItem) {
        BoundingBox bb = gameItem.getBoundingBox();
        EndPoint min, max;
        Set<BoundingBox> possibleCollisions = new HashSet<>();

        int i=0;
        while (i!= xAxis.size()){
            if (xAxis.get(i).isMin){//min
                min = xAxis.get(i);
                max = min.getBox().xMax;

            } else {//max
                max = xAxis.get(i);
                min = max.getBox().xMin;
            }

            if (bb.xMin.position <= min.position){
                if(bb.xMax.position >= min.position ) possibleCollisions.add(min.getBox());
            } else {
                if (bb.xMin.position <= max.position) possibleCollisions.add(min.getBox());
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
            if (yAxis.get(i).isMin){//min
                min = yAxis.get(i);
                max = min.getBox().yMax;

            } else {//max
                max = yAxis.get(i);
                min = max.getBox().yMin;
            }

            if (bb.yMin.position <= min.position){
                if(bb.yMax.position >= min.position ) possibleCollisions.add(min.getBox());
            } else {
                if (bb.yMin.position <= max.position) possibleCollisions.add(min.getBox());
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
            if (zAxis.get(i).isMin){//min
                min = zAxis.get(i);
                max = min.getBox().zMax;

            } else {//max
                max = zAxis.get(i);
                min = max.getBox().zMin;
            }

            if (bb.zMin.position <= min.position){
                if(bb.zMax.position >= min.position ) possibleCollisions.add(min.getBox());
            } else {
                if (bb.zMin.position <= max.position) possibleCollisions.add(min.getBox());
            }
            i++;
        }
        return possibleCollisions;
    }

    private void tryMoveX(GameItem gameItem, BoundingBox bb){
        if (gameItem.accel.x > 0){
            BoundingBox nextBb;
            int i = xAxis.indexOf(bb.xMax)+1;
            float value = bb.xMax.position;
            while (i < xAxis.size()-1 && i > -1 && xAxis.get(i).position <= value) {
                if (bb.xMax.position > xAxis.get(i).position) {
                    nextBb = xAxis.get(i).getBox();
                    if (xAxis.get(i).isMin) {//collision
                        incCollisions(bb, nextBb);
                    }
                }
                i++;
            }
            i = xAxis.indexOf(bb.xMin)+1;
            value = bb.xMin.position;
            while (i < xAxis.size()-1 && i > -1 && xAxis.get(i).position <= value) {
                if (bb.xMin.position > xAxis.get(i).position) {
                    nextBb = xAxis.get(i).getBox();
                    if (!xAxis.get(i).isMin) {//out collision
                        decCollisions(bb, nextBb);
                    }
                }
                i++;
            }
        } else if(gameItem.accel.x < 0){
            BoundingBox prevBb;
            int i = xAxis.indexOf(bb.xMin)-1;
            float value = bb.xMin.position;
            while (i > -1 && i < xAxis.size()-1 && xAxis.get(i).position >= value) {
                if (bb.xMin.position < xAxis.get(i).position) {
                    prevBb = xAxis.get(i).getBox();
                    if (!xAxis.get(i).isMin) {//collision
                        incCollisions(bb, prevBb);
                    }
                }
                i--;
            }
            i = xAxis.indexOf(bb.xMax)-1;
            value = bb.xMax.position;
            while (i > -1 && i < xAxis.size()-1 && xAxis.get(i).position >= value) {
                if (bb.xMax.position < xAxis.get(i).position) {
                    prevBb = xAxis.get(i).getBox();
                    if (xAxis.get(i).isMin) {//out collision
                        decCollisions(bb, prevBb);
                    }
                }
                i--;
            }
        }
    }

    private void tryMoveY(GameItem gameItem, BoundingBox bb){
        if (gameItem.accel.y > 0){
            BoundingBox nextBb;
            int i = yAxis.indexOf(bb.yMax)+1;
            float value = 0;
            if(i>-1 && i<yAxis.size()-1) value = yAxis.get(i).position;
            while (i < yAxis.size()-1 && i > -1 && yAxis.get(i).position <= value) {
                if (bb.yMax.position > yAxis.get(i).position) {
                    nextBb = yAxis.get(i).getBox();
                    if (yAxis.get(i).isMin) {//collision
                        incCollisions(bb, nextBb);
                    }
                }
                i++;
            }

            i = yAxis.indexOf(bb.yMin)+1;
            if (i>-1 && i<yAxis.size()-1)value = yAxis.get(i).position;
            while (i < yAxis.size()-1 && i > -1 && yAxis.get(i).position <= value) {
                if (bb.yMin.position > yAxis.get(i).position) {
                    nextBb = yAxis.get(i).getBox();
                    if (!yAxis.get(i).isMin) {//out collision
                        decCollisions(bb, nextBb);
                    }
                }
                i++;
            }
        } else if(gameItem.accel.y < 0){
            BoundingBox prevBb;
            int i = yAxis.indexOf(bb.yMin)-1;
            float value = 0;
            if(i>-1 && i<yAxis.size()-1) value = yAxis.get(i).position;
            while (i > -1 && i < yAxis.size()-1 && yAxis.get(i).position >= value) {
                if (bb.yMin.position < yAxis.get(i).position) {
                    prevBb = yAxis.get(i).getBox();
                    if (!yAxis.get(i).isMin) {//collision
                        incCollisions(bb, prevBb);
                    }
                }
                i--;
            }
            i = yAxis.indexOf(bb.yMax)-1;
            if(i>-1 && i<yAxis.size()-1) value = yAxis.get(i).position;
            while (i > -1 && i < yAxis.size()-1 && yAxis.get(i).position >= value) {
                if (bb.yMax.position < yAxis.get(i).position) {
                    prevBb = yAxis.get(i).getBox();
                    if (yAxis.get(i).isMin) {//out collision
                        decCollisions(bb, prevBb);
                    }
                }
                i--;
            }
        }
    }

    private void tryMoveZ(GameItem gameItem, BoundingBox bb){
        if (gameItem.accel.z > 0){
            BoundingBox nextBb;
            int i = zAxis.indexOf(bb.zMax)+1;
            float value = bb.zMax.position;
            while (i < zAxis.size()-1 && i > -1 && zAxis.get(i).position <= value) {
                if (bb.zMax.position > zAxis.get(i).position) {
                    nextBb = zAxis.get(i).getBox();
                    if (zAxis.get(i).isMin) {//collision
                        incCollisions(bb, nextBb);
                    }
                }
                i++;
            }
            i = zAxis.indexOf(bb.zMin)+1;
            value = bb.zMin.position;
            while (i < zAxis.size()-1 && i > -1 && zAxis.get(i).position <= value) {
                if (bb.zMin.position > zAxis.get(i).position) {
                    nextBb = zAxis.get(i).getBox();
                    if (!zAxis.get(i).isMin) {//out collision
                        decCollisions(bb, nextBb);
                    }
                }
                i++;
            }
        } else if(gameItem.accel.z < 0){
            BoundingBox prevBb;
            int i = zAxis.indexOf(bb.zMin)-1;
            float value = bb.zMin.position;
            while (i > -1 && i < zAxis.size()-1 && zAxis.get(i).position >= value) {
                if (bb.zMin.position < zAxis.get(i).position) {
                    prevBb = zAxis.get(i).getBox();
                    if (!zAxis.get(i).isMin) {//collision
                        incCollisions(bb, prevBb);
                    }
                }
                i--;
            }
            i = zAxis.indexOf(bb.zMax)-1;
            value = bb.zMax.position;
            while (i > -1 && i < zAxis.size()-1 && zAxis.get(i).position >= value) {
                if (bb.zMax.position < zAxis.get(i).position) {
                    prevBb = zAxis.get(i).getBox();
                    if (zAxis.get(i).isMin) {//out collision
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
        collisionPairs.put(bb, bb2, collisionPairs.get(bb, bb2) + 1);
    }

    private void decrement(BoundingBox bb, BoundingBox bb2) {
        collisionPairs.put(bb, bb2, collisionPairs.get(bb, bb2) - 1);
    }

    public void printAxis() {
        System.out.println("-----------------------------------------------------------");
        System.out.println(xAxis);
        System.out.println(yAxis);
        System.out.println(zAxis);
    }
}
