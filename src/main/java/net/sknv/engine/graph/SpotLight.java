package net.sknv.engine.graph;

import org.joml.Vector3f;

public class SpotLight {

    private PointLight pointLight;
    private Vector3f coneDirection;

    //in radians
    private float cutOff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        setCutOffAngle(cutOffAngle);
    }

    public SpotLight(SpotLight light) {
        this(new PointLight(light.getPointLight()), new Vector3f(light.getConeDirection()), 0);
        setCutOff(light.getCutOff());
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOff() {
        return cutOff;
    }

    //use with radians
    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    //use with degrees
    public final void setCutOffAngle(float cutOffAngle) {
        this.setCutOff((float)Math.cos(Math.toRadians(cutOffAngle)));
    }
}
