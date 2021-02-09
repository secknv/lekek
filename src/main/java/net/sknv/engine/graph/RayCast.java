package net.sknv.engine.graph;

import net.sknv.engine.entities.GameItemMesh;
import net.sknv.game.Renderer;
import org.joml.Vector3f;

public class RayCast {
    public Vector3f origin;
    public Vector3f direction;

    public RayCast(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3f intersectPlane(Vector3f origin, Vector3f normal){
        Vector3f rayToQuad = new Vector3f();
        origin.sub(this.origin, rayToQuad);

        float k = rayToQuad.dot(normal) / this.direction.dot(normal);

        if(k>=0 && k<=1){//reaches plane
            Vector3f intersectionPoint = new Vector3f();
            direction.mul(k, intersectionPoint);
            intersectionPoint.add(this.origin);
            return intersectionPoint;
        } else if(k>=1){//doesnt reach the plane needs scaling (ofc it needs! kinda dumb method but yea we'll see later)
            Vector3f intersectionPoint = new Vector3f();
            direction.mul(k, intersectionPoint);
            intersectionPoint.add(this.origin);
            return intersectionPoint;
        } //else opposite direction
        return null;
    }

    public boolean intersectsTriangle(Vector3f p1, Vector3f p2, Vector3f p3){
        Vector3f edge1 = new Vector3f();
        Vector3f edge2 = new Vector3f();
        Vector3f triangleNormal = new Vector3f();
        Vector3f perp = new Vector3f();
        Vector3f perpLength = new Vector3f();

        p1.sub(p2, edge1);
        p3.sub(p2, edge2);

        edge1.cross(edge2, triangleNormal);
        Vector3f intersectionPoint = intersectPlane(p1, triangleNormal);
        if(intersectionPoint==null) return false;

        float dotP = edge1.dot(edge2) / edge2.dot(edge2);
        edge2.mul(dotP, perp);
        perp.add(p2);
        perp.sub(p1, perpLength);

        Vector3f intersectionLength = new Vector3f();
        intersectionPoint.sub(p1, intersectionLength);

        float barycentric = (intersectionLength.dot(perpLength)) / (perpLength.dot(perpLength));
        if(barycentric>1 || barycentric<0) return false;

        //-------------------------second barycentric
        p3.sub(p1, edge1);
        p2.sub(p1, edge2);
        dotP = edge1.dot(edge2) / edge2.dot(edge2);

        edge2.mul(dotP, perp);
        perp.add(p1);
        perp.sub(p3, perpLength);
        intersectionPoint.sub(p3, intersectionLength);

        barycentric = (intersectionLength.dot(perpLength)) / (perpLength.dot(perpLength));
        if(barycentric>1 || barycentric<0) return false;

        //----------------------third barycentric
        p2.sub(p3, edge1);
        p1.sub(p3, edge2);
        dotP = edge1.dot(edge2) / edge2.dot(edge2);

        edge2.mul(dotP, perp);
        perp.add(p3);
        perp.sub(p2, perpLength);
        intersectionPoint.sub(p2, intersectionLength);

        barycentric = (intersectionLength.dot(perpLength)) / (perpLength.dot(perpLength));
        if(barycentric>1 || barycentric<0) return false;
        return true;
    }

    public boolean intersectsItem(GameItemMesh gameItem) {
        Vector3f min = gameItem.getBoundingBox().getMin().getPosition();
        Vector3f max = gameItem.getBoundingBox().getMax().getPosition();

        Vector3f[] vertex = new Vector3f[]{
                min, new Vector3f(max.x, min.y, min.z), new Vector3f(min.x, min.y, max.z),
                min, new Vector3f(min.x, max.y, min.z), new Vector3f(min.x, min.z, max.z),
                min, new Vector3f(min.x, max.y, min.z), new Vector3f(max.x, min.y, min.z),

                max, new Vector3f(max.x, max.y, min.z), new Vector3f(min.x, max.y, max.z),
                max, new Vector3f(max.x, min.y, max.z), new Vector3f(min.x, max.y, max.z),
                max, new Vector3f(max.x, min.y, max.z), new Vector3f(max.x, max.y, min.z),

                new Vector3f(min.x, max.y, min.z), new Vector3f(max.x, max.y, min.z), new Vector3f(min.x, max.y, max.z),
                new Vector3f(min.x, max.y, min.z), new Vector3f(max.x, max.y, min.z), new Vector3f(max.x, min.y, min.z),
                new Vector3f(min.x, max.y, min.z), new Vector3f(min.x, max.y, max.z), new Vector3f(min.x, min.y, max.z),

                new Vector3f(max.x, min.y, max.z), new Vector3f(max.x, min.y, min.z), new Vector3f(min.x, min.y, max.z),
                new Vector3f(max.x, min.y, max.z), new Vector3f(max.x, min.y, min.z), new Vector3f(max.x, max.y, min.z),
                new Vector3f(max.x, min.y, max.z), new Vector3f(min.x, min.y, max.z), new Vector3f(min.x, max.y, max.z),
        };

        for(int i=0; i!=vertex.length; i+=3){
            if(intersectsTriangle(vertex[i], vertex[i+1], vertex[i+2])) return true;
        }
        return false;
    }
}