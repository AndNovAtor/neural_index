/*
 * Copyright © 2016 Andrey Novikov.
 *
 * Java code is based on Sovietmade (https://github.com/Sovietmade) c++
 * code (https://github.com/Sovietmade/NeuralNetworks)
 *
 * This file is part of neural_index.
 *
 * neural_index is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * neural_index is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with neural_index.  If not, see <http://www.gnu.org/licenses/>.
 */

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