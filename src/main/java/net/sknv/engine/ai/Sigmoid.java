package net.sknv.engine.ai;

public class Sigmoid {
    private int nInputs;
    private double[] weights;
    private double bias;

    public Sigmoid(int nInputs, double[] weights, double bias) {
        this.nInputs = nInputs;
        this.weights = weights;
        this.bias = bias;
    }
}

