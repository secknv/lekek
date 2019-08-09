package net.sknv.engine.ai;

import java.util.ArrayList;
import java.util.Arrays;

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

    private void init() {
        //init hidden layers
        for(int i=0; i!=nHiddenLayers; i++){
            hiddenLayers.set(i, new Sigmoid[nNodesHiddenLayer]);
            Arrays.fill(hiddenLayers.get(i), new Sigmoid(nInputNodes, null, 0));
        }

        //init output layer
        outputLayer = new Sigmoid[nNodesOutputLayer];
        Arrays.fill(outputLayer, new Sigmoid(nNodesHiddenLayer, null, 0));
    }

}
