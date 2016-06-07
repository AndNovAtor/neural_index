package com.andnovator.neural.network;

/**
 * Created by novator on 01.11.2015.
 */

import com.andnovator.utils.Stopwatch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Neural network class.
 * An object of that type represents a neural network of several types:
 * - Single layer perceptron;
 * - Multiple layers perceptron.
 * <p>
 * There are several training algorithms available as well:
 * - Perceptron;
 * - Backpropagation.
 * <p>
 * How to use this class:
 * To be able to use neural network , you have to create an instance of that class, specifying
 * a number of input neurons, output neurons, number of hidden layers and amount of neurons in hidden layers.
 * You can also specify a type of neural network, by passing a string with a name of neural network, otherwise
 * MultiLayerPerceptron will be used. ( A training algorithm can be changed via public calls);
 * <p>
 * Once the neural network was created, all u have to do is to set the biggest MSE required to achieve during
 * the training phase ( or u can skip this step, then mMinMSE will be set to 0.01 ),
 * train the network by providing a training data with target results.
 * Afterwards u can obtain the net response by feeding the net with data;
 */

public class NeuralNetwork {

    private final int debugEachIterations = 1000;

    /**
     * A Neural Network constructor.
     * - Description:    A template constructor. T is a data type, all the nodes will operate with. Create a neural network by providing it with:
     *
     * @param inInputs                     - an integer argument - number of input neurons of newly created neural network;
     * @param inOutputs-                   an integer argument - number of output neurons of newly created neural network;
     * @param inNumOfHiddenLayers          - an integer argument - number of hidden layers of newly created neural network, default is 0;
     * @param inNumOfNeuronsInHiddenLayers - an integer argument - number of neurons in hidden layers of newly created neural network ( note that every hidden layer has the same amount of neurons), default is 0;
     * @param inTypeOfNeuralNetwork        - a char * argument - a type of neural network, we are going to create. The values may be:
     *                                     <UL>
     *                                     <LI>MultiLayerPerceptron;</LI>
     *                                     <LI>Default is MultiLayerPerceptron.</LI>
     *                                     </UL>
     *                                     - Purpose:      Creates a neural network for solving some interesting problems.
     *                                     - Prerequisites:  The template parameter has to be picked based on your input data.
     */
    public NeuralNetwork(int inInputs, int inOutputs, int inNumOfHiddenLayers, int inNumOfNeuronsInHiddenLayers, String inTypeOfNeuralNetwork) {
        if ((inInputs > 0) && (inOutputs > 0)) {
            mMinMSE = 0.01;
            mMeanSquaredError = 0;
            inputsNum = inInputs;
            outputsNum = inOutputs;
            neuronsPerHidden = inNumOfNeuronsInHiddenLayers;
            hiddenLayersNum = inNumOfHiddenLayers;
        /*
		 *		Network function's declarations for input and output neurons.
		*/
            NetworkFunction OutputNeuronsFunc = new NetworkFunction();
            NetworkFunction InputNeuronsFunc = new NetworkFunction();
		/*
		 *		At least two layers require - input and output;
		*/
            List<Neuron> outputLayer = new ArrayList<>();
            List<Neuron> inputLayer = new ArrayList<>();

		/*
		 *		This block of strcmps decides what training algorithm and neuron factory we should use as well as what
		 *		network function every node will have.
		*/
            if (inTypeOfNeuralNetwork.equals("MultiLayerPerceptron")) {
                mNeuronFactory = new PerceptronNeuronFactory();
                mTrainingAlgoritm = new Backpropagation(this);
                OutputNeuronsFunc = new BipolarSigmoid();
//                OutputNeuronsFunc = new DecimalPlusSigmoid();
                InputNeuronsFunc = new Linear();

            }
		/*
		 * 		Output layers creation
		*/
            for (int iNumOfOutputs = 0; iNumOfOutputs < inOutputs; iNumOfOutputs++) {
                outputLayer.add(mNeuronFactory.CreateOutputNeuron(OutputNeuronsFunc)); // New output neuron
            }
            mLayers.add(outputLayer);
		/*
		 * 		Hidden layers creation
		*/
            for (int i = 0; i < inNumOfHiddenLayers; i++) {
                List<Neuron> HiddenLayer = new ArrayList<>();
                for (int j = 0; j < inNumOfNeuronsInHiddenLayers; j++) {
                    Neuron hidden = mNeuronFactory.CreateHiddenNeuron(mLayers.get(0), OutputNeuronsFunc);
                    HiddenLayer.add(hidden);
                }
                mBiasLayer.add(0, mNeuronFactory.CreateInputNeuron(mLayers.get(0), InputNeuronsFunc));
                mLayers.add(0, HiddenLayer);

            }
		/*
		 *		Input layers creation
		*/
            for (int iNumOfInputs = 0; iNumOfInputs < inInputs; iNumOfInputs++) {
                inputLayer.add(mNeuronFactory.CreateInputNeuron(mLayers.get(0), InputNeuronsFunc));
            }
            mBiasLayer.add(0, mNeuronFactory.CreateInputNeuron(mLayers.get(0), InputNeuronsFunc));
            mLayers.add(0, inputLayer);

            mTrainingAlgoritm.WeightsInitialization();
        } else {
            System.out.println("Error in Neural Network constructor: The number of input and output neurons has to be more than 0!");
        }
    }

    public NeuralNetwork(int inInputs,
                         int inOutputs,
                         int inNumOfHiddenLayers,
                         int inNumOfNeuronsInHiddenLayers) {
        this(inInputs, inOutputs, inNumOfHiddenLayers, inNumOfNeuronsInHiddenLayers, "MultiLayerPerceptron");
    }


    /**
     * Public method Train.
     * - Description:    Method for training the network.
     * - Purpose:      Trains a network, so the weights on the links adjusted in the way to be able to solve problem.
     * - Prerequisites:
     *
     * @param inData   - a List of Lists with data to train with;
     * @param inTarget - a List of Lists with target data;
     *                 - the number of data samples and target samples has to be equal;
     *                 - the data and targets has to be in the appropriate order u want the network to learn.
     */

    public boolean Train(List<List<Double>> inData, List<List<Double>> inTarget) {
        int iIteration = 0;
        Stopwatch stopwatch = new Stopwatch();
        while (true) {
            ++iIteration;
            for (int i = 0; i < inData.size(); i++) {
                mTrainingAlgoritm.Train(inData.get(i), inTarget.get(i));
            }
            double MSE = this.getMSE();
            this.resetMSE();
            if (MSE < mMinMSE) {
                System.out.printf("%s| At %d iteration MSE: %.4g was achieved. SUCCESS%n", Stopwatch.formatNow(), iIteration, MSE);
                return true;
            }
            if (iIteration+1>maxTrainItNum) {
                System.out.printf("%s| At %d iteration MSE was: %.4g > minMSE (%.4g); but it's max iteration%n", Stopwatch.formatNow(), iIteration, MSE, mMinMSE);
                System.out.println("Training was stopped.");
                System.out.println("Error - training is failure!");
                return false;
            }

            // debug output
            if (iIteration % debugEachIterations == 0) {
                double nItersTimeSec = ((double) stopwatch.newLap()) / 1000.0;
                double iterPerSec = ((double) debugEachIterations) / nItersTimeSec;
                System.out.printf("%s| At %d iteration MSE: %.4g > minMSE (%.4g), continue... (%.2g iter/sec)%n", Stopwatch.formatNow(), iIteration, MSE, mMinMSE, iterPerSec);
            }
        }
    }

    /**
     * Public method GetNetResponse.
     * - Description:    Method for actually get response from net by feeding it with data.
     * - Purpose:      By calling this method u make the network evaluate the response for u.
     * - Prerequisites:
     *
     * @param inData - a List data to feed with.
     */

    public List<Double> GetNetResponse(List<Double> inData, boolean printResults) {
        List<Double> netResponse = new ArrayList<>();
        if (inData.size() != inputsNum) {
            System.out.println("Input data dimensions are wrong, expected: " + inputsNum + " elements");

            return netResponse;
        } else {
            for (int indexOfData = 0; indexOfData < this.GetInputLayer().size(); indexOfData++) {
                this.GetInputLayer().get(indexOfData).Input(inData.get(indexOfData));
            }

            for (int numOfLayers = 0; numOfLayers < mLayers.size() - 1; numOfLayers++) {
                mBiasLayer.get(numOfLayers).Input(1.0);

                for (int indexOfData = 0; indexOfData < mLayers.get(numOfLayers).size(); indexOfData++) {
                    mLayers.get(numOfLayers).get(indexOfData).Fire();
                }

                mBiasLayer.get(numOfLayers).Fire();
            }


            if (printResults) { System.out.print("Net response is: {"); }
            for (int indexOfOutputElements = 0; indexOfOutputElements < outputsNum; indexOfOutputElements++) {

			/*
			 * 		For every neuron in output layer, make it fire its sum of charges;
			 */

                double res = this.GetOutputLayer().get(indexOfOutputElements).Fire();
                netResponse.add(res);

                if (printResults) { System.out.print(res + "; "); }


            }
            if (printResults) { System.out.println(" }"); }

            this.ResetCharges();
            return netResponse;

        }
    }

    public List<Double> GetNetResponse(List<Double> inData) {
        return this.GetNetResponse(inData, false);
    }

    /**
     * Public method SetAlgorithm.
     * - Description:    Setter for algorithm of training the net.
     * - Purpose:      Can be used for dynamic change of training algorithm.
     * - Prerequisites:
     *
     * @param inTrainingAlgorithm - an existence of already created object  of type TrainAlgorithm.
     */

    void SetAlgorithm(TrainAlgorithm inTrainingAlgorithm) {
        mTrainingAlgoritm = inTrainingAlgorithm;
    }
    //void SetAlgorithm( int par = 0 )    { mTrainingAlgoritm = (par == 0) ? (new Backpropagation<T>(this)) : (new Genetic<T>(this)); }

    /**
     * Public method SetNeuronFactory.
     * - Description:    Setter for the factory, which is making neurons for the net.
     * - Purpose:      Can be used for dynamic change of neuron factory.
     * - Prerequisites:
     *
     * @param inNeuronFactory - an existence of already created object  of type NeuronFactory.
     */

    void SetNeuronFactory(NeuronFactory inNeuronFactory) {
        mNeuronFactory = inNeuronFactory;
    }

    /**
     * Public method ShowNetworkState.
     * - Description:    Prints current state to the standard output: weight of every link.
     * - Purpose:      Can be used for monitoring the weights change during training of the net.
     * - Prerequisites:  None.
     */

    void ShowNetworkState() {
        System.out.println();
        for (int indOfLayer = 0; indOfLayer < mLayers.size(); indOfLayer++) {
            System.out.println("Layer index: " + indOfLayer);
            for (int indOfNeuron = 0; indOfNeuron < mLayers.get(indOfLayer).size(); indOfNeuron++) {
                System.out.println("  Neuron index: " + indOfNeuron);
                mLayers.get(indOfLayer).get(indOfNeuron).ShowNeuronState();
            }
            if (indOfLayer < mBiasLayer.size()) {
                System.out.println("  Bias: ");
                mBiasLayer.get(indOfLayer).ShowNeuronState();
            }
        }
    }

    /**
     * Public method getMinMSE.
     * - Description:    Returns the biggest MSE required to achieve during the training phase.
     * - Purpose:      Can be used for getting the biggest MSE required to achieve during the training phase.
     * - Prerequisites:  None.
     */

    public double getMinMSE() {
        return mMinMSE;
    }

    /**
     * Public method setMinMSE.
     * - Description:    Setter for the biggest MSE required to achieve during the training phase.
     * - Purpose:      Can be used for setting the biggest MSE required to achieve during the training phase.
     * - Prerequisites:
     *
     * @param inMinMse - double value, the biggest MSE required to achieve during the training phase.
     */

    public void setMinMSE(double inMinMse) {
        mMinMSE = inMinMse;
    }

    /**
     * Protected method GetLayer.
     * - Description:    Getter for the layer by index of that layer.
     * - Purpose:      Can be used by inner implementation for getting access to neural network's layers.
     * - Prerequisites:
     *
     * @param inInd -  an integer index of layer.
     */

    List<Neuron> GetLayer(int inInd) {
        return mLayers.get(inInd);
    }
    List<List<Neuron>> GetAllLayers() {
        return mLayers;
    }

    /**
     * Protected method size.
     * - Description:    Returns the number of layers in the network.
     * - Purpose:      Can be used by inner implementation for getting number of layers in the network.
     * - Prerequisites:  None.
     */

    public int size() {
        return mLayers.size();
    }

    /**
     * Protected method GetNumOfOutputs.
     * - Description:    Returns the number of units in the output layer.
     * - Purpose:      Can be used by inner implementation for getting number of units in the output layer.
     * - Prerequisites:  None.
     */

    protected List<Neuron> GetOutputLayer() {
        return mLayers.get(mLayers.size()-1);
    }

    /**
     * Protected method GetInputLayer.
     * - Description:    Returns the input layer.
     * - Purpose:      Can be used by inner implementation for getting the input layer.
     * - Prerequisites:  None.
     */

    protected List<Neuron> GetInputLayer() {
        return mLayers.get(0);
    }

    /**
     * Protected method GetBiasLayer.
     * - Description:    Returns the List of Biases.
     * - Purpose:      Can be used by inner implementation for getting List of Biases.
     * - Prerequisites:  None.
     */

    protected List<Neuron> GetBiasLayer() {
        return mBiasLayer;
    }

    /**
     * Protected method UpdateWeights.
     * - Description:    Updates the weights of every link between the neurons.
     * - Purpose:      Can be used by inner implementation for updating the weights of links between the neurons.
     * - Prerequisites:  None, but only makes sense, when its called during the training phase.
     */

    protected void UpdateWeights() {
        for (List<Neuron> mLayer : mLayers) {
            mLayer.forEach(Neuron::PerformWeightsUpdating);
        }
    }

    /**
     * Protected method ResetCharges.
     * - Description:    Resets the neuron's data received during iteration of net training.
     * - Purpose:      Can be used by inner implementation for reset the neuron's data between iterations.
     * - Prerequisites:  None, but only makes sense, when its called during the training phase.
     */

    protected void ResetCharges() {
        for (List<Neuron> mLayer : mLayers) {
            mLayer.forEach(Neuron::ResetSumOfCharges);
        }
        mBiasLayer.forEach(Neuron::ResetSumOfCharges);
    }

    /**
     * Protected method addMSE.
     * - Description:    Changes MSE during the training phase.
     * - Purpose:      Can be used by inner implementation for changing MSE during the training phase.
     * - Prerequisites:
     *
     * @param inPortion -  a double amount of MSE to be add.
     */

    void addMSE(double inPortion) {
        mMeanSquaredError += inPortion;
    }

    /**
     * Protected method getMSE.
     * - Description:    Getter for MSE value.
     * - Purpose:      Can be used by inner implementation for getting access to the MSE value.
     * - Prerequisites:  None.
     */

    double getMSE() {
        return mMeanSquaredError;
    }

    /**
     * Protected method resetMSE.
     * - Description:    Resets MSE value.
     * - Purpose:      Can be used by inner implementation for resetting MSE value.
     * - Prerequisites:  None.
     */

    void resetMSE() {
        mMeanSquaredError = 0;
    }

    NeuronFactory mNeuronFactory;       /*!< Member, which is responsible for creating neurons @see SetNeuronFactory */
    TrainAlgorithm mTrainingAlgoritm;      /*!< Member, which is responsible for the way the network will trained @see SetAlgorithm */
    List<List<Neuron>> mLayers = new ArrayList<>();   /*!< Inner representation of neural networks */
    List<Neuron> mBiasLayer = new ArrayList<>();          /*!< Container for biases */

    public int getInputsNum() { return inputsNum; }
    public int getOutputsNum() { return outputsNum; }
    public int getHiddenLayersNum() { return hiddenLayersNum; }
    public int getHiddenLayersSize() { return neuronsPerHidden; }
    public int getMaxTrainItNum() { return maxTrainItNum; }
    public int getAllNeuronsNum() {
        return inputsNum + hiddenLayersNum * neuronsPerHidden + outputsNum;
    }
    private int getBiasLinkNum() { return hiddenLayersNum*neuronsPerHidden + outputsNum; }
    private int getSimpleNeuronsLinksNum() { return (inputsNum + (hiddenLayersNum -1)*neuronsPerHidden + outputsNum)* neuronsPerHidden; }

    public void setMaxTrainItNum(int maxTrainIterationsNum) {
        if (maxTrainIterationsNum>100) {
            maxTrainItNum = maxTrainIterationsNum;
        }
    }
    public List<double[]> exportNetworkWeightsArr() {

        // 1. связи каждого сдвигового нейрона с остальными
        DoubleStream biasLinks = mBiasLayer.stream().flatMap(this::exportNeuronWeights).mapToDouble(x -> x);

        // 2. связи каждого слоя
        DoubleStream otherNeuronsLinks = mLayers.stream()
                .limit(mLayers.size()-1)
                .flatMap( layer ->layer.stream().flatMap(this::exportNeuronWeights) )
                .mapToDouble(x -> x);

        return Arrays.asList(biasLinks.toArray(), otherNeuronsLinks.toArray());
    }
    private Stream<Double> exportNeuronWeights(Neuron n) {
        return n.GetLinksToNeurons().stream()
                .map(NeuralLink::GetWeight);
    }
    public List<List<Double>> exportNetworkWeightsLst() {
        // 1. связи каждого сдвигового нейрона с остальными
        Stream<Double> biasLinks = mBiasLayer.stream().flatMap(this::exportNeuronWeights);
        // 2. связи каждого слоя
        Stream<Double> otherNeuronsLinks = mLayers.stream()
                .limit(mLayers.size()-1)
                .flatMap( layer ->layer.stream().flatMap(this::exportNeuronWeights) );
        return Arrays.asList(biasLinks.collect(toList()), otherNeuronsLinks.collect(toList()));
    }

    /**
     *
     * @param biasesWeights
     * @param simpleNeuronWeights
     * @throws IllegalArgumentException если размеры массивов некорректны
     */
    public void importNetworkWeights(List<Double> biasesWeights, List<Double> simpleNeuronWeights) throws IllegalArgumentException { // {
        if ((simpleNeuronWeights.size() != getSimpleNeuronsLinksNum())
                || (biasesWeights.size() != getBiasLinkNum())) {
            throw new IllegalArgumentException("NN loading is failed - input parameters is not same as NN object which invoked this loading");
        }
        final double maxWeight = mTrainingAlgoritm.getMaxAbsWeight();
        simpleNeuronWeights.stream().filter(w -> Math.abs(w) > maxWeight).findFirst().ifPresent(w -> {
            throw new IllegalArgumentException("NN loading is failed - all inner neuron weights should be in [-1; 1] but "+w+" found");
        });
        setNetworkWeights(biasesWeights, simpleNeuronWeights);
    }

    public void importNetworkWeights(double[] biasesWeights, double[] otherWeights) {
        List<Double> bias = Arrays.stream(biasesWeights).boxed().collect(Collectors.toList());
        List<Double> other = Arrays.stream(otherWeights).boxed().collect(Collectors.toList());
        importNetworkWeights(bias, other);
    }
    void setNetworkWeights(List<Double> biasesWeights, List<Double> otherWeights) {
        Iterator<Double> biasesIter = biasesWeights.iterator();
        mBiasLayer.forEach(biasNeuron ->
            biasNeuron.GetLinksToNeurons().forEach( link ->
                link.SetWeight(biasesIter.next())
            )
        );
        Iterator<Double> otherIter = otherWeights.iterator();
        mLayers.stream()
               .limit(mLayers.size() - 1)
               .forEach(neuronsLayer ->
                   neuronsLayer.forEach(neuron ->
                       neuron.GetLinksToNeurons().forEach(link ->
                           link.SetWeight(otherIter.next())
                       )
        ));
    }
    public static NeuralNetwork loadNetwork(List<Double> biasesWeights, List<Double> simpleNeuronWeights, int inputs, int outputs,
                                               int hiddenLayerNum, int neuronsInHiddenL) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(inputs, outputs, hiddenLayerNum, neuronsInHiddenL);
        neuralNetwork.importNetworkWeights(biasesWeights, simpleNeuronWeights);
        return neuralNetwork;
    }

    /*private Stream<Double> exportNeuronLinks(Neuron<T> n, Function<NeuralLink<T>,Double> lamdFunct) {
        return n.GetLinksToNeurons().stream()
                .map(lamdFunct);
    }*/


    int inputsNum, outputsNum, hiddenLayersNum, neuronsPerHidden; // Number of inputs, outputs, hidden layers and units in every hidden layer
    double mMeanSquaredError;  // Mean Squared Error which is changing every iteration of the training
    double mMinMSE;          // The biggest Mean Squared Error required for training to stop
    int maxTrainItNum = 60000;
}
