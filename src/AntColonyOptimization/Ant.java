/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AntColonyOptimization;

import NeuralNetwork.ConfusionMatrix;
import NeuralNetwork.NeuralNetwork;
import java.util.Map;

/**
 *
 * @author Azhary Arliansyah
 */
public class Ant {

    private int trailLength;
    private double pheromone;
    private double Q;
    private Map<String, double[][]> currentVertex;
    private NeuralNetwork nn;
    
    public Ant(double Q, Map<String, double[][]> currentVertex) {
        this.trailLength = 0;
        this.Q = Q;
        this.currentVertex = currentVertex;
    }

    private double updatePheromone() {
    	if (this.trailLength <= 0) {
    		return 0.0;
    	}

    	return this.Q / this.trailLength;
    }
    
    public ConfusionMatrix move(double[][] features, double[][] classes, 
            int numHiddenNeuron, double learningRate, int epoch, 
            double splitRatio) {
        this.nn = new NeuralNetwork(features, classes, 
                numHiddenNeuron, learningRate, epoch, splitRatio);
        return this.nn.fitOpt();
    }
    
    public NeuralNetwork getNn() {
        return this.nn;
    }
}
