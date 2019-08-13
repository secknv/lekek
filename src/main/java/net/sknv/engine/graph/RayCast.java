package net.sknv.engine.graph;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class RayCast {
    private ShaderProgram shader;
    public Vector3f origin;
    public Vector3f direction;

    public RayCast(ShaderProgram shader, Vector3f origin, Vector3f direction) {
        this.shader = shader;
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public void drawNormalisedRay(){
        Vector3f ray = new Vector3f();
        origin.add(direction, ray);
        GraphUtils.drawLine(shader, origin, ray);
    }

    public void drawScaledRay(int k, Vector4f color) {
        Vector3f scaled = new Vector3f();
        origin.add(direction.mul(k, scaled), scaled);
        GraphUtils.drawLine(shader, color, origin, scaled);
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

        //triangle visual aid
        GraphUtils.drawLine(shader, new Vector4f(1,0,0,0), p1,p2);
        GraphUtils.drawLine(shader, new Vector4f(1,0,0,0), p2,p3);
        GraphUtils.drawLine(shader, new Vector4f(1,0,0,0), p3,p1);

        p1.sub(p2, edge1);
        p3.sub(p2, edge2);
        float dotP = edge1.dot(edge2) / edge2.dot(edge2);

        edge1.cross(edge2, triangleNormal);
        Vector3f intersectionPoint = intersectPlane(p1, triangleNormal);

        edge2.mul(dotP, perp);
        perp.add(p2);

        perp.sub(p1, perpLength); //all fine

        Vector3f intersectionLength = new Vector3f();
        intersectionPoint.sub(p1, intersectionLength);

        float barycentric = (intersectionLength.dot(perpLength)) / (perpLength.dot(perpLength));

        GraphUtils.drawLine(shader, new Vector4f(1,1,0,0), p1, intersectionPoint);
        GraphUtils.drawLine(shader, new Vector4f(1,1,0,0), p1, perp);

        //-------------------------second barycentric
        p3.sub(p1, edge1);
        p2.sub(p1, edge2);
        dotP = edge1.dot(edge2) / edge2.dot(edge2);

        edge2.mul(dotP, perp);
        perp.add(p1);
        perp.sub(p3, perpLength);
        intersectionPoint.sub(p3, intersectionLength);

        float barycentric2 = (intersectionLength.dot(perpLength)) / (perpLength.dot(perpLength));

        GraphUtils.drawLine(shader, new Vector4f(1,1,0,0), p3, intersectionPoint);
        GraphUtils.drawLine(shader, new Vector4f(1,1,0,0), p3, perp);


        //----------------------third barycentric
        p2.sub(p3, edge1);
        p1.sub(p3, edge2);
        dotP = edge1.dot(edge2) / edge2.dot(edge2);

        edge2.mul(dotP, perp);
        perp.add(p3);
        perp.sub(p2, perpLength);
        intersectionPoint.sub(p2, intersectionLength);

        float barycentric3 = (intersectionLength.dot(perpLength)) / (perpLength.dot(perpLength));

        GraphUtils.drawLine(shader, new Vector4f(1,1,0,0), p2, intersectionPoint);
        GraphUtils.drawLine(shader, new Vector4f(1,1,0,0), p2, perp);

        System.out.println(barycentric + " " + barycentric2 + " " + barycentric3);

        return barycentric<=1 && barycentric>=0 && barycentric2<=1 && barycentric2>=0 && barycentric3<=1 && barycentric3>=0;
    }
}
