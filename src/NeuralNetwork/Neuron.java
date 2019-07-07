/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NeuralNetwork;

import Control.MathFx;

/**
 *
 * @author Azhary Arliansyah
 */
public class Neuron {
    
    public enum Type {
        INPUT, HIDDEN, OUTPUT, BIAS
    };
    
    private double weight;
    private double value;
    private double mappedValue;
    private Type type;
    
    public Neuron(double weight, Type type) {
        this.weight = weight;
        this.type = type;
    }
    
    public Neuron(Type type) {
        this.type = type;
        this.weight = MathFx.randUniform(1);
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public void setMappedValue(double mappedValue) {
        this.mappedValue = mappedValue;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public double getValue() {
        return this.value;
    }
    
    public double getMappedValue() {
        return this.mappedValue;
    }
}
