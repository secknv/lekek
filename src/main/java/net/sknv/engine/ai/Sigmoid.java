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

    private double calc(double[] inputs){
        double weightedSum = 0;
        for(int i = 0; i!= weights.length; i++){
            weightedSum += weights[i] * inputs[i];
        }
        return sigmoid(weightedSum + bias);
    }

    private double sigmoid(double x) {
        return (1 / (1 + Math.exp(-x)));
    }
}