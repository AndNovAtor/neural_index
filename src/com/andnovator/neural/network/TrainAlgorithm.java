package com.andnovator.neural.network;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generic training algorithm for neural network and its implementations, e.g. back propagation
 * Created by novator on 01.11.2015.
 */
public interface TrainAlgorithm {  //TODO: Is it OK, if it's interface?
    double Train(List<Double> inData, List<Double> inTarget); // +down to_do
    void WeightsInitialization();
    default double getMaxAbsWeight() {
        return 1.0;
    }
}


class Backpropagation implements TrainAlgorithm   {

    Backpropagation(NeuralNetwork inNeuralNetwork) { mNeuralNetwork = inNeuralNetwork; }
    public double Train(List<Double> inData, List<Double> inTarget) {
        /*
	 * 		Check incoming data
	*/

        double result = 0;
        if( inData.size() != mNeuralNetwork.inputsNum || inTarget.size() != mNeuralNetwork.outputsNum){
            System.out.println("Input data dimensions are wrong, expected: " + mNeuralNetwork.inputsNum + " elements");

            return -1;
        }
        else{

		/*
		 * 		Step 3. Feedforward: Each input unit receives input signal and
		 * 		broadcast this signal to all units in the layer above (the hidden units)
		*/

            // подаём входы
            List<Neuron> inputLayer = mNeuralNetwork.GetInputLayer();
            for(int indexOfData = 0; indexOfData < mNeuralNetwork.inputsNum; indexOfData++){
                //System.out.println("input" << indexOfData << ": " << inData.get(indexOfData));
                inputLayer.get(indexOfData).Input(inData.get(indexOfData));
            }

            // для каждого слоя, кроме последнего...
            for(int numOfLayer = 0; numOfLayer < mNeuralNetwork.size() - 1; numOfLayer++){
                // n-й слой
                List<Neuron> layer = mNeuralNetwork.GetLayer(numOfLayer);
                // bias-нейрон от этого слоя
                Neuron biasNeuron = mNeuralNetwork.GetBiasLayer().get(numOfLayer);

                biasNeuron.Input(1.0);
                //System.out.println("BiasInput"  );
                //System.out.println("Layer: " << numOfLayer);
                //System.out.println("IndexOfNeuron: " << indexOfNeuronInLayer);
                layer.forEach(Neuron::Fire);
                //System.out.println("Bias: " << numOfLayer);
                biasNeuron.Fire();
                for (NeuralLink biasLink : biasNeuron.GetLinksToNeurons()) {
                    biasLink.SetLastTranslatedSignal(1);
                }
            }

		/*
		 * 		Step 5. Each output unit applies its activation function to compute its output
		 * 		signal.
		*/
            List<Neuron> outputLayer = mNeuralNetwork.GetOutputLayer();
            outputLayer.forEach(Neuron::Fire);
            /*List<Double> netResponseYk = new ArrayList<>();
            for(int indexOfOutputElements = 0; indexOfOutputElements < mNeuralNetwork.outputsNum; indexOfOutputElements++){
                mNeuralNetwork.GetOutputLayer().get(indexOfOutputElements).Fire();
                double Yk = mNeuralNetwork.GetOutputLayer().get(indexOfOutputElements).Fire();
                netResponseYk.add(Yk);
            }*/

		/*
		 * 		Step 6. Backpropagation of error
		 *		Computing error information for each output unit.
		*/

            for(int indexOfData = 0; indexOfData < mNeuralNetwork.outputsNum; indexOfData++){
                result = outputLayer.get(indexOfData).PerformTrainingProcess(inTarget.get(indexOfData));
                mNeuralNetwork.addMSE(result);
            }
		/*
		 *		FIXME: Net should perform training process not only for last layer and layer before last, but also for any
		 *		layers except input one, so fix it DUDE!
		*/
            for(int iIndOfLayer = mNeuralNetwork.size() - 2; iIndOfLayer > 0 ; iIndOfLayer--){
                List<Neuron> layer = mNeuralNetwork.GetLayer(iIndOfLayer);
                for (Neuron neuron : layer) {
                    neuron.PerformTrainingProcess(0);
                }
            }
            mNeuralNetwork.UpdateWeights();
            mNeuralNetwork.ResetCharges();
            return result;
        }
    }
    public void WeightsInitialization() {
        NguyenWidrowWeightsInitialization();
    }

    @Override
    public double getMaxAbsWeight() {
        double dNumOfInputs = mNeuralNetwork.getInputsNum();
        double dNumOfHiddens = mNeuralNetwork.getHiddenLayersSize();
        double degree = 1.0 / dNumOfInputs ;
        return 0.7*(Math.pow( dNumOfHiddens , degree ) );
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

        double dScaleFactor = getMaxAbsWeight();

        for(int layerInd = 0; layerInd < mNeuralNetwork.size(); layerInd++){
            for(int neuronInd = 0; neuronInd < mNeuralNetwork.GetLayer(layerInd).size(); neuronInd++){
                Neuron currentNeuron = mNeuralNetwork.GetLayer(layerInd).get(neuronInd);
                for(int linkInd = 0; linkInd < currentNeuron.GetNumOfLinks(); linkInd++){
                    NeuralLink currentNeuralLink = currentNeuron.get(linkInd);
                    double pseudoRandWeight = ThreadLocalRandom.current().nextDouble(-0.5,0.5);
                    currentNeuralLink.SetWeight(pseudoRandWeight);
                    //System.out.println("layerInd: " << layerInd << ", neuronInd: " << neuronInd << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight());
                }
            }
        }


        for(int neuronHiddenInd = 0; neuronHiddenInd < mNeuralNetwork.GetLayer(1).size(); neuronHiddenInd++){
            //Neuron * currentHiddenNeuron = mNeuralNetwork.GetLayer(1).get(neuronHiddenInd);
            double dSquaredNorm = 0;

            for(int neuronInputInd = 0; neuronInputInd < mNeuralNetwork.GetLayer(0).size(); neuronInputInd++){
                Neuron currentInputNeuron = mNeuralNetwork.GetLayer(0).get(neuronInputInd);

                NeuralLink currentNeuralLink = currentInputNeuron.get(neuronHiddenInd);
                dSquaredNorm +=Math.pow(currentNeuralLink.GetWeight(),2.0);
            }

            double dNorm = Math.sqrt(dSquaredNorm);

            for(int neuronInputInd = 0; neuronInputInd < mNeuralNetwork.GetLayer(0).size(); neuronInputInd++){
                Neuron currentInputNeuron = mNeuralNetwork.GetLayer(0).get(neuronInputInd);

                NeuralLink currentNeuralLink = currentInputNeuron.get(neuronHiddenInd);
                double dNewWeight = ( dScaleFactor * ( currentNeuralLink.GetWeight() ) ) / dNorm;
                currentNeuralLink.SetWeight(dNewWeight);
            }

        }

        for(int layerInd = 0; layerInd < mNeuralNetwork.size() - 1; layerInd++){

            Neuron Bias = mNeuralNetwork.GetBiasLayer().get(layerInd);
            for(int linkInd = 0; linkInd < Bias.GetNumOfLinks(); linkInd++){
                NeuralLink currentNeuralLink = Bias.get(linkInd);
                double pseudoRandWeight = ThreadLocalRandom.current().nextDouble(-dScaleFactor, dScaleFactor);
                //float pseudoRandWeight = 0;
                currentNeuralLink.SetWeight(pseudoRandWeight);
                //System.out.println("layerInd Bias: " << layerInd  << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight());
            }
        }
    }

    protected void CommonInitialization() {
/*
	 * 		Step 0. Initialize weights ( Set to small values )
	*/
	/*
	 *		For every layer, for every neuron and bias in that layer,  for every link in that neuron, set the weight
	 *		to random number from 0 to 1;
	 *
	*/

        for(int layerInd = 0; layerInd < mNeuralNetwork.size(); layerInd++){
            for(int neuronInd = 0; neuronInd < mNeuralNetwork.GetLayer(layerInd).size(); neuronInd++){
                Neuron currentNeuron = mNeuralNetwork.GetLayer(layerInd).get(neuronInd);
                for(int linkInd = 0; linkInd < currentNeuron.GetNumOfLinks(); linkInd++){
                    NeuralLink currentNeuralLink = currentNeuron.get(linkInd);
                    float pseudoRandWeight = -0.5f + ThreadLocalRandom.current().nextFloat(); // rand(-0.5..0.5)
                    currentNeuralLink.SetWeight(pseudoRandWeight);
                }
            }
        }
        for(int layerInd = 0; layerInd < mNeuralNetwork.size() - 1; layerInd++){

            Neuron Bias = mNeuralNetwork.GetBiasLayer().get(layerInd);
            for(int linkInd = 0; linkInd < Bias.GetNumOfLinks(); linkInd++){
                NeuralLink currentNeuralLink = Bias.get(linkInd);
                float pseudoRandWeight = -0.5f + ThreadLocalRandom.current().nextFloat();
                currentNeuralLink.SetWeight(pseudoRandWeight);
            }
        }
    }
    protected NeuralNetwork mNeuralNetwork;
}


class Genetic implements TrainAlgorithm {
    Genetic(NeuralNetwork inNeuralNetwork) {}
    public double Train(List<Double> inData, List<Double> inTarget) {return 0;}
    public void WeightsInitialization() { }

    //protected void NguyenWidrowWeightsInitialization() {}
    //protected void CommonInitialization() {}
    protected  NeuralNetwork mNeuralNetwork;

}
