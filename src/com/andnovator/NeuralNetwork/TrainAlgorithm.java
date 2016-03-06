package com.andnovator.NeuralNetwork;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by novator on 01.11.2015.
 */
public interface TrainAlgorithm<T> {  //TODO: Is it OK, if it's interface?
    double Train(ArrayList<T> inData, ArrayList<T> inTarget); // +down to_do
    void WeightsInitialization();  // TODO: "=0" in C++ - why near void?6 (if it's interface, this is no matter)
}


class Backpropagation<T> implements TrainAlgorithm<T>  // TODO:
{

    Backpropagation(NeuralNetwork<T> inNeuralNetwork) {}
    public double Train(ArrayList<T> inData, ArrayList<T> inTarget) {

    }
    public void WeightsInitialization() {

    }

    protected void NguyenWidrowWeightsInitialization() {
        /*
         * 		Step 0. Initialize weights ( Set to small values )
        */


        /*
         *		For every layer, for every neuron and bias in that layer,  for every link in that neuron, set the weight
         *		to random number from 0 to 1;
         *
        */

        double dNumOfInputs = mNeuralNetwork.mInputs;
        double dNumOfHiddens = mNeuralNetwork.mHidden;
        double degree = 1.0 / dNumOfInputs ;
        double dScaleFactor = 0.7*(Math.pow( dNumOfHiddens , degree ) );

        for(int layerInd = 0; layerInd < mNeuralNetwork.size(); layerInd++){
            for(int neuronInd = 0; neuronInd < mNeuralNetwork.GetLayer(layerInd).size(); neuronInd++){
                Neuron<T> currentNeuron = mNeuralNetwork.GetLayer(layerInd).get(neuronInd);
                for(int linkInd = 0; linkInd < currentNeuron.GetNumOfLinks(); linkInd++){
                    NeuralLink<T> currentNeuralLink = currentNeuron.get(linkInd);
                    double pseudoRandWeight = ThreadLocalRandom.current().nextDouble(-0.5,0.5);
                    currentNeuralLink.SetWeight(pseudoRandWeight);

                    //std::cout << "layerInd: " << layerInd << ", neuronInd: " << neuronInd << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight() << std::endl;

                }
            }
        }


        for(int neuronHiddenInd = 0; neuronHiddenInd < mNeuralNetwork.GetLayer(1).size(); neuronHiddenInd++){
            //Neuron * currentHiddenNeuron = mNeuralNetwork.GetLayer(1).get(neuronHiddenInd);

            double dSquaredNorm = 0;

            for(int neuronInputInd = 0; neuronInputInd < mNeuralNetwork.GetLayer(0).size(); neuronInputInd++){
                Neuron<T> currentInputNeuron = mNeuralNetwork.GetLayer(0).get(neuronInputInd);

                NeuralLink<T> currentNeuralLink = currentInputNeuron.get(neuronHiddenInd);

                dSquaredNorm +=Math.pow(currentNeuralLink.GetWeight(),2.0);
            }

            double dNorm = Math.sqrt(dSquaredNorm);

            for(int neuronInputInd = 0; neuronInputInd < mNeuralNetwork.GetLayer(0).size(); neuronInputInd++){
                Neuron<T> currentInputNeuron = mNeuralNetwork.GetLayer(0).get(neuronInputInd);

                NeuralLink<T> currentNeuralLink = currentInputNeuron.get(neuronHiddenInd);

                double dNewWeight = ( dScaleFactor * ( currentNeuralLink.GetWeight() ) ) / dNorm;
                currentNeuralLink.SetWeight(dNewWeight);
            }

        }

        for(int layerInd = 0; layerInd < mNeuralNetwork.size() - 1; layerInd++){

            Neuron<T> Bias = mNeuralNetwork.GetBiasLayer().get(layerInd);
            for(int linkInd = 0; linkInd < Bias.GetNumOfLinks(); linkInd++){
                NeuralLink<T> currentNeuralLink = Bias.get(linkInd);
                double pseudoRandWeight = ThreadLocalRandom.current().nextDouble(-dScaleFactor,dScaleFactor);
                //float pseudoRandWeight = 0;
                currentNeuralLink.SetWeight(pseudoRandWeight);
                //std::cout << "layerInd Bias: " << layerInd  << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight() << std::endl;
            }
        }
    }
    protected void CommonInitialization() {

    }
    protected NeuralNetwork<T> mNeuralNetwork;
}


class Genetic<T> implements TrainAlgorithm<T>
{
    Genetic(NeuralNetwork<T> inNeuralNetwork) {}
    public double Train(ArrayList<T> inData, ArrayList<T> inTarget) {return 0;}
    public void WeightsInitialization() { }

    //protected void NguyenWidrowWeightsInitialization() {}
    //protected void CommonInitialization() {}
    protected  NeuralNetwork<T> mNeuralNetwork;

}
