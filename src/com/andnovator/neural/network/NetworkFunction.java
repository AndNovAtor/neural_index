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

/**
 * Created by novator on 01.11.2015.
 */
public class NetworkFunction {
    public NetworkFunction(){}
    public double Process(double inParam) {return 0;}
    public double Derivative(double inParam) {return 0;}
}

class Linear extends NetworkFunction {
    public Linear() {}
    public double Process(double inParam) {return inParam;}
    public double Derivative(double inParam) {return 0;}
}


class Sigmoid extends NetworkFunction {
    public Sigmoid() {}
    public double Process(double inParam) {
        return (1/(1+Math.exp(-inParam)));
    }
    public double Derivative(double inParam) {
        return (this.Process(inParam)*(1-this.Process(inParam)));
    }
}

class BipolarSigmoid extends NetworkFunction {
    public BipolarSigmoid() {}
    public double Process(double inParam) {
        return (2/(1+Math.exp(-inParam))-1);
    }
    public double Derivative(double inParam) {
        return (0.5*(1+this.Process(inParam))*(1-this.Process(inParam)));
    }
}
class DecimalPlusSigmoid extends NetworkFunction {
    public DecimalPlusSigmoid() {}
    public double Process(double inParam) {
        return (10/(1+Math.exp(-inParam))-1);
    }
    public double Derivative(double inParam) {
        return (0.1*(this.Process(inParam)+1)*(10-this.Process(inParam)-1));
    }
}
