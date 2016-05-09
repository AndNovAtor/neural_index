package com.andnovator.neural.network;

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

    Backpropagation(NeuralNetwork<T> inNeuralNetwork) { mNeuralNetwork = inNeuralNetwork; }
    public double Train(ArrayList<T> inData, ArrayList<T> inTarget) {
        /*
	 * 		Check incoming data
	*/

        double result = 0;
        if( inData.size() != mNeuralNetwork.mInputs || inTarget.size() != mNeuralNetwork.mOutputs ){
            System.out.println("Input data dimensions are wrong, expected: " + mNeuralNetwork.mInputs + " elements");

            return -1;
        }
        else{

		/*
		 * 		Step 3. Feedforward: Each input unit receives input signal and
		 * 		broadcast this signal to all units in the layer above (the hidden units)
		*/

            for(int indexOfData = 0; indexOfData < mNeuralNetwork.mInputs; indexOfData++){
                //System.out.println("input" << indexOfData << ": " << inData.get(indexOfData));
                // TODO: !!!!!!  Hack with casting!!!!!!!!!!!!!!!!!!!!!!!!!!
                mNeuralNetwork.GetInputLayer().get(indexOfData).Input((Double)inData.get(indexOfData));
            }


            for(int numOfLayer = 0; numOfLayer < mNeuralNetwork.size() - 1; numOfLayer++){
                mNeuralNetwork.GetBiasLayer().get(numOfLayer).Input(1.0);
                //System.out.println("BiasInput"  );
                //System.out.println("Layer: " << numOfLayer);
                for(int indexOfNeuronInLayer = 0; indexOfNeuronInLayer < mNeuralNetwork.GetLayer(numOfLayer).size(); indexOfNeuronInLayer++){
                    //System.out.println("IndexOfNeuron: " << indexOfNeuronInLayer);
                    mNeuralNetwork.GetLayer(numOfLayer).get(indexOfNeuronInLayer).Fire();
                }
                //System.out.println("Bias: " << numOfLayer);
                mNeuralNetwork.GetBiasLayer().get(numOfLayer).Fire();
                for(int i = 0; i < mNeuralNetwork.GetBiasLayer().get(numOfLayer).GetNumOfLinks(); i++){
                    mNeuralNetwork.GetBiasLayer().get(numOfLayer).GetLinksToNeurons().get(i).SetLastTranslatedSignal(1);
                }
            }

		/*
		 * 		Step 5. Each output unit applies its activation function to compute its output
		 * 		signal.
		*/


            ArrayList<Double> netResponseYk = new ArrayList<>();
            for(int indexOfOutputElements = 0; indexOfOutputElements < mNeuralNetwork.mOutputs; indexOfOutputElements++){

                double Yk = mNeuralNetwork.GetOutputLayer().get(indexOfOutputElements).Fire();
                netResponseYk.add(Yk);

            }

		/*
		 * 		Step 6. Backpropagation of error
		 *		Computing error information for each output unit.
		*/

            for(int indexOfData = 0; indexOfData < mNeuralNetwork.mOutputs; indexOfData++){
                result = mNeuralNetwork.GetOutputLayer().get(indexOfData).PerformTrainingProcess((Double)inTarget.get(indexOfData)); //TODO Casting!!!
                mNeuralNetwork.AddMSE(result);
            }


		/*
		 *		FIXME: Net should perform training process not only for last layer and layer before last, but also for any
		 *		layers except input one, so fix it DUDE!
		*/

            for(int iIndOfLayer = mNeuralNetwork.size() - 2; iIndOfLayer > 0 ; iIndOfLayer--){
                for(int indexOfNeuron = 0; indexOfNeuron < mNeuralNetwork.GetLayer(iIndOfLayer).size(); indexOfNeuron++){
                    mNeuralNetwork.GetLayer(iIndOfLayer).get(indexOfNeuron).PerformTrainingProcess(0);
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
                    //System.out.println("layerInd: " << layerInd << ", neuronInd: " << neuronInd << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight());
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
                Neuron<T> currentNeuron = mNeuralNetwork.GetLayer(layerInd).get(neuronInd);
                for(int linkInd = 0; linkInd < currentNeuron.GetNumOfLinks(); linkInd++){
                    NeuralLink<T> currentNeuralLink = currentNeuron.get(linkInd);
                    float pseudoRandWeight = -0.5f + ThreadLocalRandom.current().nextFloat(); // rand(-0.5..0.5)
                    //float pseudoRandWeight = 0;
                    currentNeuralLink.SetWeight(pseudoRandWeight);

                    //std::cout << "layerInd: " << layerInd << ", neuronInd: " << neuronInd << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight() << std::endl;

                }
            }
        }
        for(int layerInd = 0; layerInd < mNeuralNetwork.size() - 1; layerInd++){

            Neuron<T> Bias = mNeuralNetwork.GetBiasLayer().get(layerInd);
            for(int linkInd = 0; linkInd < Bias.GetNumOfLinks(); linkInd++){
                NeuralLink<T> currentNeuralLink = Bias.get(linkInd);
                float pseudoRandWeight = -0.5f + ThreadLocalRandom.current().nextFloat();
                //float pseudoRandWeight = 0;
                currentNeuralLink.SetWeight(pseudoRandWeight);

                //std::cout << "layerInd Bias: " << layerInd  << ", linkInd: " << linkInd << ", Weight: " << currentNeuralLink.GetWeight() << std::endl;

            }
        }
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
