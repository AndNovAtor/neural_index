package com.andnovator.neural.network;

import java.util.List;

/**
 * Created by novator on 01.11.2015.
 */
public class NeuronFactory {
    public NeuronFactory(){}
    public Neuron CreateInputNeuron(List<Neuron> inNeuronsLinkTo, NetworkFunction inNetFunc ) {return null;}
    public Neuron CreateOutputNeuron(NetworkFunction inNetFunc ) {return null;}
    public Neuron CreateHiddenNeuron(List<Neuron> inNeuronsLinkTo, NetworkFunction inNetFunc ) {return null;}

}

class PerceptronNeuronFactory extends NeuronFactory {

    public PerceptronNeuronFactory(){}
    public Neuron CreateInputNeuron(List<Neuron> inNeuronsLinkTo, NetworkFunction inNetFunc ) { return new Neuron(inNeuronsLinkTo, inNetFunc); }
    public Neuron CreateOutputNeuron(NetworkFunction inNetFunc ){ return new OutputLayerNeuronDecorator(new Neuron(inNetFunc)); }
    public Neuron CreateHiddenNeuron(List<Neuron> inNeuronsLinkTo, NetworkFunction inNetFunc ){ return new HiddenLayerNeuronDecorator(new Neuron(inNeuronsLinkTo, inNetFunc)); }
}