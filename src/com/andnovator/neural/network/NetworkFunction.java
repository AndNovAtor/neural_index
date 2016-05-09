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
