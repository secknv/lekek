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

    private void init() { //sigmoid init values must be changed !!
        //init first hidden (input fed) layer
        hiddenLayers.set(0, new Sigmoid[nNodesHiddenLayer]);
        Arrays.fill(hiddenLayers.get(0), new Sigmoid(nInputNodes, null, 0));
        //init subsequent layers
        for(int i=1; i!=nHiddenLayers; i++){
            hiddenLayers.set(i, new Sigmoid[nNodesHiddenLayer]);
            Arrays.fill(hiddenLayers.get(i), new Sigmoid(hiddenLayers.get(i-1).length, null, 0));
        }

        //init output layer
        outputLayer = new Sigmoid[nNodesOutputLayer];
        Arrays.fill(outputLayer, new Sigmoid(nNodesHiddenLayer, null, 0));
    }

}
