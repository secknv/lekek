package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.graph.GraphUtils;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Boid extends GameItem {
    private float maxAccel = .01f;
    private float maxTurnRatio = .01f;
    private Vector3f f,t,r;

    public Boid(Mesh mesh) {
        super(mesh);
        accel = new Vector3f( 0, 0, -1f);
        f = accel;
        t = new Vector3f(0,1,0);
        r = new Vector3f(1,0,0);
    }

    @Override
    public Vector3f tryMove(){
        prevPos = new Vector3f(pos.x, pos.y, pos.z);
        Vector3f scaledAccel = new Vector3f();
        accel.mul(maxAccel, scaledAccel);
        pos = pos.add(scaledAccel);
        boundingBox.transform(this);
        Vector3f yz = new Vector3f(0, f.y,f.z);
        Vector3f x = new Vector3f(f.z,0,0);
        setRot((float) (yz.angleSigned(new Vector3f(0,0,1), x) - Math.PI/2), 0 ,0);
        return scaledAccel;
    }

    @Override
    public void move(){

    }

    public void inUp(){
        accel.rotateAxis(maxTurnRatio, r.x, r.y, r.z);
        t.rotateAxis(maxTurnRatio, r.x, r.y, r.z);
    }

    public void inDown() {
        accel.rotateAxis(-maxTurnRatio, r.x, r.y, r.z);
        t.rotateAxis(-maxTurnRatio, r.x, r.y, r.z);
    }

    public void inLeft() {
        accel.rotateAxis(maxTurnRatio, t.x, t.y, t.z);
        r.rotateAxis(maxTurnRatio, t.x, t.y, t.z);
    }

    public void inRight() {
        accel.rotateAxis(-maxTurnRatio, t.x, t.y, t.z);
        r.rotateAxis(-maxTurnRatio, t.x, t.y, t.z);
    }

    public void drawSelfAxis(ShaderProgram shader, Matrix4f viewMatrix) {
        Vector3f fin = new Vector3f();
        pos.add(r, fin);
        GraphUtils.drawLine(shader, viewMatrix, new Vector4f(255,0,0,0), pos, fin);
        pos.add(t, fin);
        GraphUtils.drawLine(shader, viewMatrix, new Vector4f(0,255,0,0), pos, fin);
        pos.add(f, fin);
        GraphUtils.drawLine(shader, viewMatrix, new Vector4f(0,0,255,0), pos, fin);
    }
}
