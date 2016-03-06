package com.andnovator.NeuralNetwork;

import java.util.ArrayList;

/**
 * Created by novator on 01.11.2015.
 */
class NeuronFactory<T>
{
    public NeuronFactory(){}
    public Neuron<T> CreateInputNeuron(ArrayList<Neuron<T>> inNeuronsLinkTo, NetworkFunction inNetFunc ) {return null;}
    public Neuron<T> CreateOutputNeuron(NetworkFunction inNetFunc ) {return null;}
    public Neuron<T> CreateHiddenNeuron(ArrayList<Neuron<T >> inNeuronsLinkTo, NetworkFunction inNetFunc ) {return null;}

};

class PerceptronNeuronFactory<T> extends NeuronFactory<T>
{

    public PerceptronNeuronFactory(){}
    public Neuron<T> CreateInputNeuron( ArrayList<Neuron<T>> inNeuronsLinkTo, NetworkFunction inNetFunc ) { return new Neuron<T>(inNeuronsLinkTo, inNetFunc); }
    public Neuron<T> CreateOutputNeuron( NetworkFunction inNetFunc ){ return new OutputLayerNeuronDecorator<T>( new Neuron<T>( inNetFunc ) ); }
    public Neuron<T> CreateHiddenNeuron(ArrayList<Neuron<T >> inNeuronsLinkTo, NetworkFunction inNetFunc ){ return new HiddenLayerNeuronDecorator<T>( new Neuron<T>(inNeuronsLinkTo, inNetFunc) ); }
}