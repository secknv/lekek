package net.sknv.engine.physics;

import net.sknv.engine.GameItem;
import net.sknv.engine.Scene;
import net.sknv.engine.physics.colliders.BoundingBox;
import net.sknv.engine.physics.collisionDetection.SPCollision;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PhysicsEngine {

    private SPCollision sweepPrune = new SPCollision();
    private ArrayList<GameItem> items;

    public PhysicsEngine() {
    }

    public void addGameItem(GameItem gameItem) {
        sweepPrune.addItem(gameItem);
    }

    public void simulate(Scene scene){
        items = scene.getGameItems();

       // applyForces(scene);
        //update();
        //detectCollisions(gameItems);
        //solveCollisions();

        /*
        while (!items.isEmpty()){
            GameItem gameItem = items.poll();
            if(gameItem.getVelocity().length() != 0){ //game item has vel
                System.out.println(gameItem.velocity);
                detectCollisions(gameItem);
            }
        }
         */
    }

    private void applyForces(Scene scene) {
        for (GameItem gameItem : items){
            gameItem.applyForce(scene.getGravity());
        }
    }

    private void detectCollisions(GameItem gameItem) {
        //calculate step
        Vector3f step = gameItem.getVelocity().mul(0.1f);

        Set<BoundingBox> col = sweepPrune.checkStepCollisions(gameItem, step);

        if (col.isEmpty()) {
            //no collisions, perform movement
            gameItem.translate(step);
            gameItem.getVelocity().zero();
        } else {
            handleCollisions(gameItem, col);
        }
    }

    private void handleCollisions(GameItem gameItem, Set<BoundingBox> colliders){
        Vector3f step = gameItem.getVelocity().mul(0.1f); // CHANGE THIS! step should be calculated from gameitem properties thus gameitem.getStep
        Vector3f possibleStep = calculatePossibleStep(gameItem, colliders.iterator().next(), step); //could calculate for every item but cba as for now
        Vector3f intersection = new Vector3f();
        Vector3f nIntersection = new Vector3f();

        step.sub(possibleStep, intersection);

        Set<BoundingBox> staticItems = new HashSet<>();
        for(BoundingBox bb : colliders) { //define possible step and remove components from intersect
            if (!bb.getGameItem().isMovable()) {
                //static collider
                staticItems.add(bb);
                Set<BoundingBox> colX = null, colY = null, colZ = null;

                colX = sweepPrune.checkStepCollisions(gameItem, new Vector3f(step.x, 0, 0));
                colY = sweepPrune.checkStepCollisions(gameItem, new Vector3f(0, step.y, 0));
                colZ = sweepPrune.checkStepCollisions(gameItem, new Vector3f(0, 0, step.z));

                if (colX.isEmpty() && colY.isEmpty() && colZ.isEmpty()) {
                    //collision onto static no movement
                    return;
                }

                if (!colX.isEmpty()) intersection.x = 0; else possibleStep.x = step.x;
                if (!colY.isEmpty()) intersection.y = 0; else possibleStep.y = step.y;
                if (!colZ.isEmpty()) intersection.z = 0; else possibleStep.z = step.z;
            }
        }

        //we should have possible step and intersection calculated
        colliders.removeAll(staticItems);
        intersection.div(colliders.size(), nIntersection);

        //if all collider were static
        if(colliders.isEmpty()){
            //perform movement
            gameItem.translate(possibleStep);
            gameItem.getVelocity().zero();
            return;
        }

        //apply nInt to all movable colliders
        for(BoundingBox bbCol : colliders) {
            //calculate resulting effect based on velocity , item materials and nIntersect
            collisionResponse(gameItem, bbCol.getGameItem(), step, nIntersection);
        }

    }

    private void collisionResponse(GameItem agent, GameItem subject, Vector3f step, Vector3f intersect){
        float agentP = intersect.length() * agent.getMass();
        float subjectP = intersect.length() * subject.getMass();

        if(agentP >= subjectP) ;
    }

    private Vector3f calculatePossibleStep(GameItem gameItem, BoundingBox bb, Vector3f step) {
        float x = 0;
        float y = 0;
        float z = 0;

        if(step.x>0) x = bb.getMin().getX() - gameItem.getBoundingBox().getMax().getX();
        if(step.x<0) x = bb.getMax().getX() - gameItem.getBoundingBox().getMin().getX();

        if(step.y>0) y = bb.getMin().getY() - gameItem.getBoundingBox().getMax().getY();
        if(step.y<0) y = bb.getMax().getY() - gameItem.getBoundingBox().getMin().getY();


        if(step.z>0) z = bb.getMin().getZ() - gameItem.getBoundingBox().getMax().getZ();
        if(step.z<0) z = bb.getMax().getZ() - gameItem.getBoundingBox().getMin().getZ();

        Vector3f intersection = new Vector3f(x,y,z);
        step.sub(intersection, intersection);

        return intersection;
    }

}
