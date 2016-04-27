package com.andnovator.NeuralNetwork;

/**
 * Created by novator on 01.11.2015.
 */

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *  Neural network class.
 *  An object of that type represents a neural network of several types:
 *  - Single layer perceptron;
 *  - Multiple layers perceptron.
 *
 *   There are several training algorithms available as well:
 *   - Perceptron;
 *   - Backpropagation.
 *
 *   How to use this class:
 *   To be able to use neural network , you have to create an instance of that class, specifying
 *   a number of input neurons, output neurons, number of hidden layers and amount of neurons in hidden layers.
 *   You can also specify a type of neural network, by passing a string with a name of neural network, otherwise
 *   MultiLayerPerceptron will be used. ( A training algorithm can be changed via public calls);
 *
 *   Once the neural network was created, all u have to do is to set the biggest MSE required to achieve during
 *   the training phase ( or u can skip this step, then mMinMSE will be set to 0.01 ),
 *   train the network by providing a training data with target results.
 *   Afterwards u can obtain the net response by feeding the net with data;
 *
 */

class NeuralNetwork<T>
{
    /**
     *     A Neural Network constructor.
     *     - Description:    A template constructor. T is a data type, all the nodes will operate with. Create a neural network by providing it with:
     *               @param inInputs - an integer argument - number of input neurons of newly created neural network;
     *               @param inOutputs- an integer argument - number of output neurons of newly created neural network;
     *               @param inNumOfHiddenLayers - an integer argument - number of hidden layers of newly created neural network, default is 0;
     *               @param inNumOfNeuronsInHiddenLayers - an integer argument - number of neurons in hidden layers of newly created neural network ( note that every hidden layer has the same amount of neurons), default is 0;
     *               @param inTypeOfNeuralNetwork - a char * argument - a type of neural network, we are going to create. The values may be:
     *               <UL>
     *                 <LI>MultiLayerPerceptron;</LI>
     *                 <LI>Default is MultiLayerPerceptron.</LI>
     *              </UL>
     *     - Purpose:      Creates a neural network for solving some interesting problems.
     *     - Prerequisites:  The template parameter has to be picked based on your input data.
     *
     */
    public NeuralNetwork(int inInputs, int inOutputs, int inNumOfHiddenLayers, int inNumOfNeuronsInHiddenLayers, String inTypeOfNeuralNetwork) {
        if ((inInputs> 0) && (inOutputs> 0)){
            mMinMSE			  = 0.01;
            mMeanSquaredError = 0;
            mInputs 		  =	inInputs;
            mOutputs 		  = inOutputs;
            mHidden 		  = inNumOfNeuronsInHiddenLayers;
		/*
		 *		Network function's declarations for input and output neurons.
		*/
            NetworkFunction OutputNeuronsFunc = new NetworkFunction();
            NetworkFunction InputNeuronsFunc = new NetworkFunction();
		/*
		 *		At least two layers require - input and output;
		*/
            ArrayList<Neuron<T>> outputLayer = new ArrayList<>();
            ArrayList<Neuron<T>> inputLayer = new ArrayList<>();

		/*
		 *		This block of strcmps decides what training algorithm and neuron factory we should use as well as what
		 *		network function every node will have.
		*/
            if (inTypeOfNeuralNetwork.equals("MultiLayerPerceptron")){
                mNeuronFactory = new PerceptronNeuronFactory<T>();
                mTrainingAlgoritm = new Backpropagation<T>(this);
                OutputNeuronsFunc = new BipolarSigmoid();
                InputNeuronsFunc = new Linear();

            }
		/*
		 * 		Output layers creation
		*/
            for(int iNumOfOutputs = 0; iNumOfOutputs < inOutputs; iNumOfOutputs++){
                outputLayer.add( mNeuronFactory.CreateOutputNeuron(OutputNeuronsFunc) ); // New output neuron
            }
            mLayers.add(outputLayer);
		/*
		 * 		Hidden layers creation
		*/
            for(int i = 0; i < inNumOfHiddenLayers; i++){
                ArrayList<Neuron<T>> HiddenLayer = new ArrayList<>();
                for(int j = 0; j < inNumOfNeuronsInHiddenLayers; j++ ){
                    Neuron<T> hidden = mNeuronFactory.CreateHiddenNeuron(mLayers.get(0), OutputNeuronsFunc);
                    HiddenLayer.add(hidden);
                }
                mBiasLayer.add(0,mNeuronFactory.CreateInputNeuron(mLayers.get(0), InputNeuronsFunc));
                mLayers.add(0,HiddenLayer);

            }
		/*
		 *		Input layers creation
		*/
            for(int iNumOfInputs = 0; iNumOfInputs < inInputs; iNumOfInputs++){
                inputLayer.add(mNeuronFactory.CreateInputNeuron(mLayers.get(0), InputNeuronsFunc));
            }
            mBiasLayer.add(0,mNeuronFactory.CreateInputNeuron(mLayers.get(0),InputNeuronsFunc));
            mLayers.add(0,inputLayer);

            mTrainingAlgoritm.WeightsInitialization();
        }
        else{
            System.out.println("Error in Neural Network constructor: The number of input and output neurons has to be more than 0!");
        }
    }
    public NeuralNetwork(int inInputs,
                  int inOutputs,
                  int inNumOfHiddenLayers,
                  int inNumOfNeuronsInHiddenLayers) {
      this(inInputs, inOutputs, inNumOfHiddenLayers, inNumOfNeuronsInHiddenLayers, "MultiLayerPerceptron") ;
    }
    public NeuralNetwork(int inInputs,
                  int inOutputs,
                  int inNumOfHiddenLayers
    ) {
        this(inInputs, inOutputs, inNumOfHiddenLayers, 0, "MultiLayerPerceptron") ;
    }
    public NeuralNetwork(int inInputs,
                  int inOutputs
    ) {
        this(inInputs, inOutputs, 0, 0, "MultiLayerPerceptron") ;
    }


    /**
     *     Public method Train.
     *    - Description:    Method for training the network.
     *    - Purpose:      Trains a network, so the weights on the links adjusted in the way to be able to solve problem.
     *    - Prerequisites:
     *      @param inData   - a ArrayList of ArrayLists with data to train with;
     *      @param inTarget - a ArrayList of ArrayLists with target data;
     *                - the number of data samples and target samples has to be equal;
     *                - the data and targets has to be in the appropriate order u want the network to learn.
     */

    public boolean Train(ArrayList<ArrayList<T> > inData, ArrayList<ArrayList<T>> inTarget ) {
        boolean trues = true;
        int iIteration = 0;
        while (trues) {
            iIteration++;
            for (int i = 0; i < inData.size(); i++) {
                mTrainingAlgoritm.Train(inData.get(i), inTarget.get(i));
            }
            double MSE = this.GetMSE();
            if (MSE < mMinMSE) {
                System.out.println("At " + iIteration + " iteration MSE: " + MSE + " was achieved");
                trues = false;
            }
            this.ResetMSE();
        }
        //return mTrainingAlgoritm.Train( inData,inTarget);
        return trues;
    }
    /**
     *     Public method GetNetResponse.
     *    - Description:    Method for actually get response from net by feeding it with data.
     *    - Purpose:      By calling this method u make the network evaluate the response for u.
     *    - Prerequisites:
     *      @param inData   - a ArrayList data to feed with.
     */

    public ArrayList<Integer> GetNetResponse(ArrayList<T> inData )
    {
        ArrayList<Integer> netResponse = new ArrayList<>();
        if(inData.size() != mInputs){
            System.out.println("Input data dimensions are wrong, expected: "+mInputs+" elements");

            return netResponse;
        }
        else{
            for(int indexOfData = 0; indexOfData < this.GetInputLayer().size(); indexOfData++){
                this.GetInputLayer().get(indexOfData).Input((Double)inData.get(indexOfData)); // TODO: (Double) - it's "cycle"!! What to do with <T> (there's Input(double))
            }

            for(int numOfLayers = 0; numOfLayers < mLayers.size() - 1; numOfLayers++){
                mBiasLayer.get(numOfLayers).Input(1.0);

                for(int indexOfData = 0; indexOfData < mLayers.get(numOfLayers).size(); indexOfData++){
                    mLayers.get(numOfLayers).get(indexOfData).Fire();
                }

                mBiasLayer.get(numOfLayers).Fire();
            }


            System.out.println("Net response is: {");
            for(int indexOfOutputElements = 0; indexOfOutputElements < mOutputs; indexOfOutputElements++){

			/*
			 * 		For every neuron in output layer, make it fire its sum of charges;
			 */

                double res = this.GetOutputLayer().get(indexOfOutputElements).Fire();

                System.out.println("res: "+res);


            }
            System.out.println(" }");

            this.ResetCharges();
            return netResponse;

        }
    }

    /**
     *     Public method SetAlgorithm.
     *    - Description:    Setter for algorithm of training the net.
     *    - Purpose:      Can be used for dynamic change of training algorithm.
     *    - Prerequisites:
     *      @param inTrainingAlgorithm   - an existence of already created object  of type TrainAlgorithm.
     */

    void SetAlgorithm( TrainAlgorithm<T> inTrainingAlgorithm )    { mTrainingAlgoritm = inTrainingAlgorithm; }
    //void SetAlgorithm( int par = 0 )    { mTrainingAlgoritm = (par == 0) ? (new Backpropagation<T>(this)) : (new Genetic<T>(this)); }

    /**
     *     Public method SetNeuronFactory.
     *    - Description:    Setter for the factory, which is making neurons for the net.
     *    - Purpose:      Can be used for dynamic change of neuron factory.
     *    - Prerequisites:
     *      @param inNeuronFactory   - an existence of already created object  of type NeuronFactory.
     */

    void SetNeuronFactory( NeuronFactory<T> inNeuronFactory )    { mNeuronFactory = inNeuronFactory; }

    /**
     *     Public method ShowNetworkState.
     *    - Description:    Prints current state to the standard output: weight of every link.
     *    - Purpose:      Can be used for monitoring the weights change during training of the net.
     *    - Prerequisites:  None.
     */

    void ShowNetworkState()
    {
        System.out.println();
        for(int indOfLayer = 0; indOfLayer < mLayers.size(); indOfLayer++) {
            System.out.println("Layer index: "+indOfLayer);
            for(int indOfNeuron = 0; indOfNeuron < mLayers.get(indOfLayer).size(); indOfNeuron++){
                System.out.println("  Neuron index: "+indOfNeuron);
                mLayers.get(indOfLayer).get(indOfNeuron).ShowNeuronState();
            }
            if(indOfLayer < mBiasLayer.size()){
                System.out.println("  Bias: ");
                mBiasLayer.get(indOfLayer).ShowNeuronState();
            }
        }
    }

    /**
     *     Public method GetMinMSE.
     *    - Description:    Returns the biggest MSE required to achieve during the training phase.
     *    - Purpose:      Can be used for getting the biggest MSE required to achieve during the training phase.
     *    - Prerequisites:  None.
     */

    double      GetMinMSE( )       { return mMinMSE; }

    /**
     *     Public method SetMinMSE.
     *    - Description:    Setter for the biggest MSE required to achieve during the training phase.
     *    - Purpose:      Can be used for setting the biggest MSE required to achieve during the training phase.
     *    - Prerequisites:
     *      @param inMinMse   - double value, the biggest MSE required to achieve during the training phase.
     */

    void SetMinMSE( double inMinMse ) { mMinMSE = inMinMse; }

    /**
     *     Friend class.
     */

    //friend class       Hebb<T>;

    /**
     *     Friend class.
     */

    //friend class       Backpropagation<T>;

    //friend class       Genetic<T>;

    /**
     *     Protected method GetLayer.
     *    - Description:    Getter for the layer by index of that layer.
     *    - Purpose:      Can be used by inner implementation for getting access to neural network's layers.
     *    - Prerequisites:
     *      @param inInd   -  an integer index of layer.
     */

    ArrayList<Neuron<T>>  GetLayer( int inInd )    { return mLayers.get(inInd); }

    /**
     *     Protected method size.
     *    - Description:    Returns the number of layers in the network.
     *    - Purpose:      Can be used by inner implementation for getting number of layers in the network.
     *    - Prerequisites:  None.
     */

    protected int      size( ) { return mLayers.size( ); }

    /**
     *     Protected method GetNumOfOutputs.
     *    - Description:    Returns the number of units in the output layer.
     *    - Purpose:      Can be used by inner implementation for getting number of units in the output layer.
     *    - Prerequisites:  None.
     */

    protected ArrayList<Neuron<T>>  GetOutputLayer( )      { return mLayers.get(mLayers.size( )-1); }

    /**
     *     Protected method GetInputLayer.
     *    - Description:    Returns the input layer.
     *    - Purpose:      Can be used by inner implementation for getting the input layer.
     *    - Prerequisites:  None.
     */

    protected ArrayList<Neuron<T>>  GetInputLayer( )      { return mLayers.get(0); }

    /**
     *     Protected method GetBiasLayer.
     *    - Description:    Returns the ArrayList of Biases.
     *    - Purpose:      Can be used by inner implementation for getting ArrayList of Biases.
     *    - Prerequisites:  None.
     */

    protected ArrayList<Neuron<T>>   GetBiasLayer( )       { return mBiasLayer; }

    /**
     *     Protected method UpdateWeights.
     *    - Description:    Updates the weights of every link between the neurons.
     *    - Purpose:      Can be used by inner implementation for updating the weights of links between the neurons.
     *    - Prerequisites:  None, but only makes sense, when its called during the training phase.
     */

    protected void UpdateWeights()
    {
        for(int indOfLayer = 0; indOfLayer < mLayers.size(); indOfLayer++){
            for(int indOfNeuron = 0; indOfNeuron < mLayers.get(indOfLayer).size(); indOfNeuron++){
                mLayers.get(indOfLayer).get(indOfNeuron).PerformWeightsUpdating();
            }
        }
    }

    /**
     *     Protected method ResetCharges.
     *    - Description:    Resets the neuron's data received during iteration of net training.
     *    - Purpose:      Can be used by inner implementation for reset the neuron's data between iterations.
     *    - Prerequisites:  None, but only makes sense, when its called during the training phase.
     */

    protected void ResetCharges()
    {
        for(int i = 0; i < mLayers.size(); i++ ){
            for(int indexOfOutputElements = 0; indexOfOutputElements < mLayers.get(i).size(); indexOfOutputElements++){
                mLayers.get(i).get(indexOfOutputElements).ResetSumOfCharges();


            }
            //mBiasLayer.get(i).ResetSumOfCharges();
        }
        for(int i = 0; i < mLayers.size()-1; i++ ){
            mBiasLayer.get(i).ResetSumOfCharges();
        }
    }

    /**
     *     Protected method AddMSE.
     *    - Description:    Changes MSE during the training phase.
     *    - Purpose:      Can be used by inner implementation for changing MSE during the training phase.
     *    - Prerequisites:
     *      @param inPortion   -  a double amount of MSE to be add.
     */

    void AddMSE( double inPortion )   { mMeanSquaredError += inPortion; }

    /**
     *     Protected method GetMSE.
     *    - Description:    Getter for MSE value.
     *    - Purpose:      Can be used by inner implementation for getting access to the MSE value.
     *    - Prerequisites:  None.
     */

    double GetMSE( )       { return mMeanSquaredError; }

    /**
     *     Protected method ResetMSE.
     *    - Description:    Resets MSE value.
     *    - Purpose:      Can be used by inner implementation for resetting MSE value.
     *    - Prerequisites:  None.
     */

    void ResetMSE( )       { mMeanSquaredError = 0; }


    NeuronFactory<T>    mNeuronFactory;       /*!< Member, which is responsible for creating neurons @see SetNeuronFactory */
    TrainAlgorithm<T> mTrainingAlgoritm;      /*!< Member, which is responsible for the way the network will trained @see SetAlgorithm */
    LinkedList<ArrayList<Neuron<T>>>   mLayers = new LinkedList<>();   /*!< Inner representation of neural networks */
    ArrayList<Neuron<T>> mBiasLayer = new ArrayList<>() ;          /*!< Container for biases */
    int   mInputs, mOutputs, mHidden;      /*!< Number of inputs, outputs and hidden units */
    double     mMeanSquaredError;        /*!< Mean Squared Error which is changing every iteration of the training*/
    double     mMinMSE;          /*!< The biggest Mean Squared Error required for training to stop*/
};