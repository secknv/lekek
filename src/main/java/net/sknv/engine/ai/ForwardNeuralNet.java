package net.sknv.engine.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ForwardNeuralNet {
    private int nInputNodes;
    private int nHiddenLayers;
    private int nNodesHiddenLayer;
    private int nNodesOutputLayer;
    private ArrayList<Sigmoid[]> hiddenLayers = new ArrayList<>();
    private Sigmoid[] outputLayer;

    public ForwardNeuralNet(int nInputNodes, int nHiddenLayers, int nNodesHiddenLayer, int nNodesOutputLayer) {
        this.nInputNodes = nInputNodes;
        this.nHiddenLayers = nHiddenLayers;
        this.nNodesHiddenLayer = nNodesHiddenLayer;
        this.nNodesOutputLayer = nNodesOutputLayer;

        init();
    }

    private void init() { //sigmoid init values must be changed !!
        Random r = new Random();

        //init first hidden (input fed) layer
        hiddenLayers.set(0, new Sigmoid[nNodesHiddenLayer]);
        Arrays.fill(hiddenLayers.get(0), new Sigmoid(nInputNodes, initWeights(nInputNodes, r), 2 * (r.nextDouble() - 0.5)));

        //init subsequent layers
        for(int i=1; i!=nHiddenLayers; i++){
            hiddenLayers.set(i, new Sigmoid[nNodesHiddenLayer]);
            int prevLayerNodes = hiddenLayers.get(i-1).length;
            Arrays.fill(hiddenLayers.get(i), new Sigmoid(prevLayerNodes, initWeights(prevLayerNodes, r), 2 * (r.nextDouble() - 0.5)));
        }

        //init output layer
        outputLayer = new Sigmoid[nNodesOutputLayer];
        Arrays.fill(outputLayer, new Sigmoid(nNodesHiddenLayer, initWeights(nNodesHiddenLayer, r), 2 * (r.nextDouble() - 0.5)));
    }

    private double[] initWeights(int nInputNodes, Random r) {
        double[] weights = new double[nInputNodes];
        for(int i=0; i!=nInputNodes; i++){
            weights[i] = 2 * (r.nextDouble() - 0.5);
        }
        return weights;
    }

}